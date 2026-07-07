package com.github.gokid96.e_commerce.rank.application;

import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import com.github.gokid96.e_commerce.rank.domain.RankInfo;
import com.github.gokid96.e_commerce.rank.domain.RankService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class RankFacadeUnitTest {

    @InjectMocks
    private RankFacade rankFacade;

    @Mock
    private RankService rankService;

    @Mock
    private ProductService productService;

    @DisplayName("최근 3일 가장 많이 팔린 상위 상품을 조회한다.")
    @Test
    void getPopularProducts() {
        given(rankService.getPopularSellRank(any()))
                .willReturn(RankInfo.PopularProducts.of(List.of(
                        RankInfo.PopularProduct.of(6L, 30L),
                        RankInfo.PopularProduct.of(5L, 20L)
                )));

        given(productService.getProducts(any()))
                .willReturn(ProductInfo.Products.of(List.of(
                        ProductInfo.Product.builder().productId(6L).productName("상품1").productPrice(1000L).build(),
                        ProductInfo.Product.builder().productId(5L).productName("상품2").productPrice(2000L).build()
                )));

        RankResult.PopularProducts result =
                rankFacade.getPopularProducts(RankCriteria.PopularProducts.ofTop5Days3());

        InOrder inOrder = inOrder(rankService, productService);
        inOrder.verify(rankService, times(1)).getPopularSellRank(any());
        inOrder.verify(productService, times(1)).getProducts(any());

        assertThat(result.getProducts()).hasSize(2)
                .extracting("productId")
                .containsExactly(6L, 5L);
    }

}
