package com.github.gokid96.e_commerce.order.application;

import com.github.gokid96.e_commerce.balance.domain.BalanceService;
import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import com.github.gokid96.e_commerce.coupon.domain.CouponService;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.payment.domain.PaymentService;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import com.github.gokid96.e_commerce.product.domain.stock.StockService;
import com.github.gokid96.e_commerce.rank.domain.RankService;
import com.github.gokid96.e_commerce.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;
    private final ProductService productService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final BalanceService balanceService;
    private final StockService stockService;
    private final PaymentService paymentService;
    private final RankService rankService;

    @Transactional
    public OrderResult.Order createOrder(OrderCriteria.Create criteria) {
        userService.getUser(criteria.getUserId());

        ProductInfo.OrderProducts orderProducts =
                productService.getOrderProducts(criteria.toProductCommand());

        Optional<Long> optionalCouponId = Optional.ofNullable(criteria.getCouponId());
        Optional<CouponInfo.UsableCoupon> optionalUsableCoupon = optionalCouponId
                .map(id -> couponService.getUsableCoupon(criteria.toCouponCommand()));
        double discountRate = optionalCouponId
                .map(couponService::getCoupon)
                .map(CouponInfo.Coupon::getDiscountRate)
                .orElse(0.0);
        Long userCouponId = optionalUsableCoupon
                .map(CouponInfo.UsableCoupon::getUserCouponId)
                .orElse(null);

        OrderInfo.Order order =
                orderService.createOrder(criteria.toOrderCommand(userCouponId, discountRate, orderProducts));

        balanceService.useBalance(criteria.toBalanceCommand(order.getTotalPrice()));
        optionalUsableCoupon.ifPresent(c -> couponService.useUserCoupon(c.getUserCouponId()));
        stockService.deductStock(criteria.toStockCommand());
        paymentService.pay(criteria.toPaymentCommand(order));
        orderService.paidOrder(order.getOrderId());
        rankService.createSellRank(criteria.toRankCommand(LocalDate.now()));

        return OrderResult.Order.of(order);
    }
}