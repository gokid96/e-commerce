package com.github.gokid96.e_commerce.order.application;

import com.github.gokid96.e_commerce.balance.domain.Balance;
import com.github.gokid96.e_commerce.balance.domain.BalanceRepository;
import com.github.gokid96.e_commerce.coupon.domain.Coupon;
import com.github.gokid96.e_commerce.coupon.domain.CouponRepository;
import com.github.gokid96.e_commerce.coupon.domain.CouponStatus;
import com.github.gokid96.e_commerce.coupon.domain.UserCoupon;
import com.github.gokid96.e_commerce.coupon.domain.UserCouponUsedStatus;
import com.github.gokid96.e_commerce.order.domain.Order;
import com.github.gokid96.e_commerce.order.domain.OrderRepository;
import com.github.gokid96.e_commerce.order.domain.OrderStatus;
import com.github.gokid96.e_commerce.product.domain.product.Product;
import com.github.gokid96.e_commerce.product.domain.product.ProductRepository;
import com.github.gokid96.e_commerce.product.domain.product.ProductSellingStatus;
import com.github.gokid96.e_commerce.product.domain.stock.Stock;
import com.github.gokid96.e_commerce.product.domain.stock.StockRepository;
import com.github.gokid96.e_commerce.support.IntegrationTestSupport;
import com.github.gokid96.e_commerce.user.domain.User;
import com.github.gokid96.e_commerce.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class OrderFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired private OrderFacade orderFacade;
    @Autowired private UserRepository userRepository;
    @Autowired private BalanceRepository balanceRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private CouponRepository couponRepository;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.create("항플"));
        balanceRepository.save(Balance.create(user.getId(), 500_000L));
        product = productRepository.save(Product.create("블랙뱃지", 100_000L, ProductSellingStatus.SELLING));
        stockRepository.save(Stock.create(product.getId(), 100));
    }

    @DisplayName("쿠폰 없이 주문 결제를 한다.")
    @Test
    void createOrderWithoutCoupon() {
        // given
        OrderCriteria.Create criteria = OrderCriteria.Create.of(user.getId(), null,
                List.of(OrderCriteria.OrderProduct.of(product.getId(), 2)));

        // when
        OrderResult.Order result = orderFacade.createOrder(criteria);

        // then
        assertThat(result.getTotalPrice()).isEqualTo(200_000L);

        Balance balance = balanceRepository.findOptionalByUserId(user.getId()).orElseThrow();
        assertThat(balance.getAmount()).isEqualTo(300_000L);

        Stock stock = stockRepository.findByProductId(product.getId());
        assertThat(stock.getQuantity()).isEqualTo(98);

        Order order = orderRepository.findById(result.getOrderId()).orElseThrow();
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @DisplayName("쿠폰을 사용해 주문 결제를 한다.")
    @Test
    void createOrderWithCoupon() {
        // given
        Coupon coupon = couponRepository.saveCoupon(
                Coupon.create("10% 쿠폰", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1)));
        UserCoupon userCoupon = couponRepository.saveUserCoupon(
                UserCoupon.create(user.getId(), coupon.getId()));

        OrderCriteria.Create criteria = OrderCriteria.Create.of(user.getId(), coupon.getId(),
                List.of(OrderCriteria.OrderProduct.of(product.getId(), 2)));

        // when
        OrderResult.Order result = orderFacade.createOrder(criteria);

        // then
        assertThat(result.getTotalPrice()).isEqualTo(180_000L);

        Balance balance = balanceRepository.findOptionalByUserId(user.getId()).orElseThrow();
        assertThat(balance.getAmount()).isEqualTo(320_000L);

        UserCoupon used = couponRepository.findUserCouponById(userCoupon.getId()).orElseThrow();
        assertThat(used.getUsedStatus()).isEqualTo(UserCouponUsedStatus.USED);
        assertThat(used.getUsedAt()).isNotNull();

        Order order = orderRepository.findById(result.getOrderId()).orElseThrow();
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }
}