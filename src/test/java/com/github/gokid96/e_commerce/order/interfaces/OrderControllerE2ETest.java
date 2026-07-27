package com.github.gokid96.e_commerce.order.interfaces;

import com.github.gokid96.e_commerce.balance.domain.Balance;
import com.github.gokid96.e_commerce.balance.domain.BalanceRepository;
import com.github.gokid96.e_commerce.order.domain.OrderClient;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrderControllerE2ETest extends E2EControllerTestSupport {

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private BalanceRepository balanceRepository;

    @MockitoBean
    private OrderClient orderClient;

    @DisplayName("주문/결제 시, 잔액이 부족해도 주문 접수는 성공한다.")
    @Test
    void orderPaymentWithInsufficientBalance() {
        User user = userRepository.save(User.create("유저"));
        balanceRepository.save(Balance.create(user.getId(), 1_000L));
        Product product1 = productRepository.save(Product.create("상품1", 100_000L, ProductSellingStatus.SELLING));
        Product product2 = productRepository.save(Product.create("상품2", 200_000L, ProductSellingStatus.SELLING));
        stockRepository.save(Stock.create(product1.getId(), 100));
        stockRepository.save(Stock.create(product2.getId(), 200));

        when(orderClient.getProducts(any())).thenReturn(List.of(
                OrderInfo.Product.of(product1.getId(), product1.getName(), product1.getPrice(), 1),
                OrderInfo.Product.of(product2.getId(), product2.getName(), product2.getPrice(), 2)
        ));

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

    @DisplayName("주문/결제 시, 재고가 부족해도 주문 접수는 성공한다.")
    @Test
    void orderPaymentWithInsufficientStock() {
        User user = userRepository.save(User.create("유저"));
        balanceRepository.save(Balance.create(user.getId(), 1_000_000L));
        Product product1 = productRepository.save(Product.create("상품1", 100_000L, ProductSellingStatus.SELLING));
        Product product2 = productRepository.save(Product.create("상품2", 200_000L, ProductSellingStatus.SELLING));
        stockRepository.save(Stock.create(product1.getId(), 100));
        stockRepository.save(Stock.create(product2.getId(), 1));

        when(orderClient.getProducts(any())).thenReturn(List.of(
                OrderInfo.Product.of(product1.getId(), product1.getName(), product1.getPrice(), 1),
                OrderInfo.Product.of(product2.getId(), product2.getName(), product2.getPrice(), 2)
        ));

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

    @DisplayName("주문/결제 시, 재고·잔액이 모두 부족해도 주문 접수는 성공한다.")
    @Test
    void orderPaymentWithInsufficientStockAndBalance() {
        User user = userRepository.save(User.create("유저"));
        balanceRepository.save(Balance.create(user.getId(), 1_000L));
        Product product1 = productRepository.save(Product.create("상품1", 100_000L, ProductSellingStatus.SELLING));
        Product product2 = productRepository.save(Product.create("상품2", 200_000L, ProductSellingStatus.SELLING));
        stockRepository.save(Stock.create(product1.getId(), 100));
        stockRepository.save(Stock.create(product2.getId(), 1));

        when(orderClient.getProducts(any())).thenReturn(List.of(
                OrderInfo.Product.of(product1.getId(), product1.getName(), product1.getPrice(), 1),
                OrderInfo.Product.of(product2.getId(), product2.getName(), product2.getPrice(), 2)
        ));

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

    @DisplayName("주문/결제 한다.")
    @Test
    void orderPayment() {
        User user = userRepository.save(User.create("유저"));
        balanceRepository.save(Balance.create(user.getId(), 1_000_000L));
        Product product1 = productRepository.save(Product.create("상품1", 100_000L, ProductSellingStatus.SELLING));
        Product product2 = productRepository.save(Product.create("상품2", 200_000L, ProductSellingStatus.SELLING));
        stockRepository.save(Stock.create(product1.getId(), 100));
        stockRepository.save(Stock.create(product2.getId(), 200));

        when(orderClient.getProducts(any())).thenReturn(List.of(
                OrderInfo.Product.of(product1.getId(), product1.getName(), product1.getPrice(), 1),
                OrderInfo.Product.of(product2.getId(), product2.getName(), product2.getPrice(), 2)
        ));

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