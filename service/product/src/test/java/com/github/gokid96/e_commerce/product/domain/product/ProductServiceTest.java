package com.github.gokid96.e_commerce.product.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @DisplayName("판매 중인 상품 목록을 조회한다.")
    @Test
    void getSellingProducts() {
        Product product1 = Product.builder().id(1L).name("상품A").price(1000L).sellStatus(ProductSellingStatus.SELLING).build();
        Product product2 = Product.builder().id(2L).name("상품B").price(2000L).sellStatus(ProductSellingStatus.SELLING).build();
        given(productRepository.findBySellStatusIn(ProductSellingStatus.forSelling()))
                .willReturn(List.of(product1, product2));

        ProductInfo.Products result = productService.getSellingProducts();

        assertThat(result.getProducts()).hasSize(2);
        assertThat(result.getProducts().get(0).getProductName()).isEqualTo("상품A");
        assertThat(result.getProducts().get(0).getProductPrice()).isEqualTo(1000L);
        assertThat(result.getProducts().get(1).getProductName()).isEqualTo("상품B");
    }

    @DisplayName("판매 중인 상품이 없으면 빈 목록을 반환한다.")
    @Test
    void getSellingProducts_empty() {
        given(productRepository.findBySellStatusIn(ProductSellingStatus.forSelling()))
                .willReturn(List.of());

        ProductInfo.Products result = productService.getSellingProducts();

        assertThat(result.getProducts()).isEmpty();
    }

    @DisplayName("주문할 상품들을 수량과 함께 조회한다.")
    @Test
    void getOrderProducts() {
        Product product1 = Product.builder().id(1L).name("상품A").price(1000L).sellStatus(ProductSellingStatus.SELLING).build();
        Product product2 = Product.builder().id(2L).name("상품B").price(2000L).sellStatus(ProductSellingStatus.SELLING).build();
        given(productRepository.findById(1L)).willReturn(product1);
        given(productRepository.findById(2L)).willReturn(product2);

        ProductCommand.OrderProducts command = ProductCommand.OrderProducts.of(List.of(
                ProductCommand.OrderProduct.of(1L, 2),
                ProductCommand.OrderProduct.of(2L, 3)
        ));

        ProductInfo.OrderProducts result = productService.getOrderProducts(command);

        assertThat(result.getProducts()).hasSize(2);
        assertThat(result.getProducts().get(0).getProductName()).isEqualTo("상품A");
        assertThat(result.getProducts().get(0).getQuantity()).isEqualTo(2);
        assertThat(result.getProducts().get(1).getQuantity()).isEqualTo(3);
    }

    @DisplayName("존재하지 않는 상품이 포함되면 주문 상품 조회에 실패한다.")
    @Test
    void getOrderProducts_notFound() {
        Product product1 = Product.builder().id(1L).name("상품A").price(1000L).sellStatus(ProductSellingStatus.SELLING).build();
        given(productRepository.findById(1L)).willReturn(product1);
        given(productRepository.findById(2L))
                .willThrow(new IllegalArgumentException("존재하지 않는 상품입니다."));

        ProductCommand.OrderProducts command = ProductCommand.OrderProducts.of(List.of(
                ProductCommand.OrderProduct.of(1L, 2),
                ProductCommand.OrderProduct.of(2L, 3)
        ));

        assertThatThrownBy(() -> productService.getOrderProducts(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 상품입니다.");
    }

    @DisplayName("판매 중이 아닌 상품은 주문할 수 없다.")
    @Test
    void getOrderProducts_notSelling() {
        Product product = Product.builder().id(1L).name("상품A").price(1000L).sellStatus(ProductSellingStatus.STOP_SELLING).build();
        given(productRepository.findById(1L)).willReturn(product);

        ProductCommand.OrderProducts command = ProductCommand.OrderProducts.of(List.of(
                ProductCommand.OrderProduct.of(1L, 1)
        ));

        assertThatThrownBy(() -> productService.getOrderProducts(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("판매 중인 상품이 아닙니다.");
    }

    @DisplayName("상품 ID 목록으로 상품을 조회한다.")
    @Test
    void getProducts() {
        Product product1 = Product.builder().id(1L).name("상품A").price(1000L).sellStatus(ProductSellingStatus.SELLING).build();
        Product product2 = Product.builder().id(2L).name("상품B").price(2000L).sellStatus(ProductSellingStatus.SELLING).build();
        given(productRepository.findById(1L)).willReturn(product1);
        given(productRepository.findById(2L)).willReturn(product2);

        ProductCommand.Products command = ProductCommand.Products.of(List.of(1L, 2L));

        ProductInfo.Products result = productService.getProducts(command);

        assertThat(result.getProducts()).hasSize(2);
        assertThat(result.getProducts().get(0).getProductId()).isEqualTo(1L);
        assertThat(result.getProducts().get(1).getProductId()).isEqualTo(2L);
    }
}
