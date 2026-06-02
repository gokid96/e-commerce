package com.github.gokid96.e_commerce.product.domain.product;

import com.github.gokid96.e_commerce.product.domain.stock.StockInfo;
import com.github.gokid96.e_commerce.product.domain.stock.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockService stockService;

    @InjectMocks
    private ProductService productService;

    @DisplayName("판매 중인 상품 목록을 재고와 함께 조회한다.")
    @Test
    void getSellingProducts() {
        // given
        Product product1 = Product.builder().id(1L).name("상품A").price(1000L).sellStatus(ProductSellingStatus.SELLING).build();
        Product product2 = Product.builder().id(2L).name("상품B").price(2000L).sellStatus(ProductSellingStatus.SELLING).build();     given(productRepository.findBySellStatusIn(ProductSellingStatus.forSelling()))
                .willReturn(List.of(product1, product2));

        given(stockService.getStock(product1.getId())).willReturn(StockInfo.Stock.of(10L, 50));
        given(stockService.getStock(product2.getId())).willReturn(StockInfo.Stock.of(20L, 30));

        // when
        ProductInfo.Products result = productService.getSellingProducts();

        // then
        assertThat(result.getProducts()).hasSize(2);
        assertThat(result.getProducts().get(0).getProductName()).isEqualTo("상품A");
        assertThat(result.getProducts().get(0).getProductPrice()).isEqualTo(1000L);
        assertThat(result.getProducts().get(0).getStock()).isEqualTo(50);
        assertThat(result.getProducts().get(1).getProductName()).isEqualTo("상품B");
        assertThat(result.getProducts().get(1).getStock()).isEqualTo(30);


    }

    @DisplayName("판매 중인 상품이 없으면 빈 목록을 반환한다.")
    @Test
    void getSellingProducts_empty() {
        // given
        given(productRepository.findBySellStatusIn(ProductSellingStatus.forSelling()))
                .willReturn(List.of());

        // when
        ProductInfo.Products result = productService.getSellingProducts();

        // then
        assertThat(result.getProducts()).isEmpty();
    }

}
