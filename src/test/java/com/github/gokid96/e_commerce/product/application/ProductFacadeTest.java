package com.github.gokid96.e_commerce.product.application;


import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.payment.domain.PaymentInfo;
import com.github.gokid96.e_commerce.payment.domain.PaymentService;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ProductFacadeTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductFacade productFacade;

    @DisplayName("최근 3일 결제완료 주문에서 판매량 상위 상품을 조회한다.")
    @Test
    void getPopularProducts() {
        // given
        given(paymentService.getCompletedOrdersBetweenDays(3))
                .willReturn(PaymentInfo.Orders.of(List.of(1L, 2L)));
        given(orderService.getTopPaidProducts(any()))
                .willReturn(OrderInfo.TopPaidProducts.of(List.of(6L, 5L)));
        given(productService.getProducts(any()))
                .willReturn(ProductInfo.Products.of(List.of(
                        ProductInfo.Product.builder().productId(6L).productName("상품1").productPrice(1000L).stock(10).build(),
                        ProductInfo.Product.builder().productId(5L).productName("상품2").productPrice(2000L).stock(20).build()
                )));

        // when
        ProductInfo.Products result = productFacade.getPopularProducts();

        // then
        InOrder inOrder = inOrder(paymentService, orderService, productService);
        inOrder.verify(paymentService, times(1)).getCompletedOrdersBetweenDays(3);
        inOrder.verify(orderService, times(1)).getTopPaidProducts(any());
        inOrder.verify(productService, times(1)).getProducts(any());

        assertThat(result.getProducts()).hasSize(2)
                .extracting("productId", "stock")
                .containsExactly(tuple(6L, 10), tuple(5L, 20));
    }

}
