package com.github.gokid96.e_commerce.order.interfaces;

import com.github.gokid96.e_commerce.balance.domain.Balance;
import com.github.gokid96.e_commerce.balance.domain.BalanceRepository;
import com.github.gokid96.e_commerce.coupon.domain.Coupon;
import com.github.gokid96.e_commerce.coupon.domain.CouponRepository;
import com.github.gokid96.e_commerce.coupon.domain.CouponStatus;
import com.github.gokid96.e_commerce.coupon.domain.UserCoupon;
import com.github.gokid96.e_commerce.product.domain.product.Product;
import com.github.gokid96.e_commerce.product.domain.product.ProductRepository;
import com.github.gokid96.e_commerce.product.domain.product.ProductSellingStatus;
import com.github.gokid96.e_commerce.product.domain.stock.Stock;
import com.github.gokid96.e_commerce.product.domain.stock.StockRepository;
import com.github.gokid96.e_commerce.support.E2EControllerTestSupport;
import com.github.gokid96.e_commerce.user.domain.User;
import com.github.gokid96.e_commerce.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

class OrderControllerE2ETest extends E2EControllerTestSupport {

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private BalanceRepository balanceRepository;
    @Autowired private CouponRepository couponRepository;

    @DisplayName("주문/결제 시, 잔액은 충분해야 한다.")
    @Test
    void orderPaymentWithInsufficientBalance() {
        User user = userRepository.save(User.create("유저"));
        balanceRepository.save(Balance.create(user.getId(), 1_000L));
        Product product1 = productRepository.save(Product.create("상품1", 100_000L, ProductSellingStatus.SELLING));
        Product product2 = productRepository.save(Product.create("상품2", 200_000L, ProductSellingStatus.SELLING));
        stockRepository.save(Stock.create(product1.getId(), 100));
        stockRepository.save(Stock.create(product2.getId(), 200));

        Map<String, Object> request = Map.of(
                "userId", user.getId(),
                "products", List.of(
                        Map.of("productId", product1.getId(), "quantity", 1),
                        Map.of("productId", product2.getId(), "quantity", 2)));

        client.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("잔액이 부족합니다.");
    }

    @DisplayName("주문/결제 시, 재고는 충분해야 한다.")
    @Test
    void orderPaymentWithInsufficientStock() {
        User user = userRepository.save(User.create("유저"));
        balanceRepository.save(Balance.create(user.getId(), 1_000_000L));
        Product product1 = productRepository.save(Product.create("상품1", 100_000L, ProductSellingStatus.SELLING));
        Product product2 = productRepository.save(Product.create("상품2", 200_000L, ProductSellingStatus.SELLING));
        stockRepository.save(Stock.create(product1.getId(), 0));
        stockRepository.save(Stock.create(product2.getId(), 0));

        Map<String, Object> request = Map.of(
                "userId", user.getId(),
                "products", List.of(
                        Map.of("productId", product1.getId(), "quantity", 1),
                        Map.of("productId", product2.getId(), "quantity", 2)));

        client.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("재고가 부족합니다.");
    }

    @DisplayName("주문/결제 시, 쿠폰은 사용 가능해야 한다.")
    @Test
    void orderPaymentWithInvalidCoupon() {
        User user = userRepository.save(User.create("유저"));
        balanceRepository.save(Balance.create(user.getId(), 1_000_000L));

        Coupon coupon = couponRepository.saveCoupon(
                Coupon.create("쿠폰명1", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1)));
        UserCoupon userCoupon = UserCoupon.create(user.getId(), coupon.getId());
        userCoupon.use();
        couponRepository.saveUserCoupon(userCoupon);

        Product product1 = productRepository.save(Product.create("상품1", 100_000L, ProductSellingStatus.SELLING));
        stockRepository.save(Stock.create(product1.getId(), 100));

        Map<String, Object> request = Map.of(
                "userId", user.getId(),
                "couponId", coupon.getId(),
                "products", List.of(Map.of("productId", product1.getId(), "quantity", 1)));

        client.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo(500)
                .jsonPath("$.message").isEqualTo("사용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("주문/결제 시, 주문 상품은 판매 중이어야 한다.")
    @Test
    void orderPaymentWithInvalidProduct() {
        User user = userRepository.save(User.create("유저"));
        balanceRepository.save(Balance.create(user.getId(), 1_000_000L));
        Product product1 = productRepository.save(Product.create("상품1", 100_000L, ProductSellingStatus.HOLD));
        Product product2 = productRepository.save(Product.create("상품2", 200_000L, ProductSellingStatus.SELLING));
        stockRepository.save(Stock.create(product1.getId(), 100));
        stockRepository.save(Stock.create(product2.getId(), 200));

        Map<String, Object> request = Map.of(
                "userId", user.getId(),
                "products", List.of(
                        Map.of("productId", product1.getId(), "quantity", 1),
                        Map.of("productId", product2.getId(), "quantity", 2)));

        client.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo(500)
                .jsonPath("$.message").isEqualTo("판매 중인 상품이 아닙니다.");
    }

    @DisplayName("주문/결제 한다.")
    @Test
    void orderPayment() {
        User user = userRepository.save(User.create("유저"));
        balanceRepository.save(Balance.create(user.getId(), 1_000_000L));
        Product product1 = productRepository.save(Product.create("상품1", 100_000L, ProductSellingStatus.SELLING));
        Product product2 = productRepository.save(Product.create("상품2", 200_000L, ProductSellingStatus.SELLING));
        stockRepository.save(Stock.create(product1.getId(), 100));
        stockRepository.save(Stock.create(product2.getId(), 200));

        Map<String, Object> request = Map.of(
                "userId", user.getId(),
                "products", List.of(
                        Map.of("productId", product1.getId(), "quantity", 1),
                        Map.of("productId", product2.getId(), "quantity", 2)));

        client.post()
                .uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("OK");
    }
}