package com.github.gokid96.e_commerce.order.application;

import com.github.gokid96.e_commerce.balance.domain.BalanceService;
import com.github.gokid96.e_commerce.coupon.domain.CouponService;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.payment.domain.PaymentService;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import com.github.gokid96.e_commerce.product.domain.stock.StockService;
import com.github.gokid96.e_commerce.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public OrderResult.Order createOrder(OrderCriteria.Create criteria) {
        userService.getUser(criteria.getUserId());

        ProductInfo.OrderProducts orderProducts =
                productService.getOrderProducts(criteria.toProductCommand());

        double discountRate = 0.0;
        if (criteria.getUserCouponId() != null) {
            discountRate = couponService.getUserCoupon(criteria.toCouponCommand()).getDiscountRate();
        }

        OrderInfo.Order order =
                orderService.createOrder(criteria.toOrderCommand(discountRate, orderProducts));

        balanceService.useBalance(criteria.toBalanceCommand(order.getTotalPrice()));
        if (criteria.getUserCouponId() != null) {
            couponService.useCoupon(criteria.toCouponCommand());
        }
        stockService.deductStock(criteria.toStockCommand());
        paymentService.pay(criteria.toPaymentCommand(order));
        orderService.paidOrder(order.getOrderId());

        return OrderResult.Order.of(order);
    }


}
