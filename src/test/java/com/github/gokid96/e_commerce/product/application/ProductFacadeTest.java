package com.github.gokid96.e_commerce.product.application;

import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.payment.domain.PaymentInfo;
import com.github.gokid96.e_commerce.payment.domain.PaymentService;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import com.github.gokid96.e_commerce.product.domain.stock.StockInfo;
import com.github.gokid96.e_commerce.product.domain.stock.StockService;
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

    @Mock
    private StockService stockService;

    @InjectMocks
    private ProductFacade productFacade;

    @DisplayName("최근 3일 결제완료 주문에서 판매량 상위 상품을 재고와 함께 조회한다.")
    @Test
    void getPopularProducts() {
        given(paymentService.getCompletedOrdersBetweenDays(3))
                .willReturn(PaymentInfo.Orders.of(List.of(1L, 2L)));
        given(orderService.getTopPaidProducts(any()))
                .willReturn(OrderInfo.TopPaidProducts.of(List.of(6L, 5L)));
        given(productService.getProducts(any()))
                .willReturn(ProductInfo.Products.of(List.of(
                        ProductInfo.Product.builder().productId(6L).productName("상품1").productPrice(1000L).build(),
                        ProductInfo.Product.builder().productId(5L).productName("상품2").productPrice(2000L).build()
                )));
        given(stockService.getStock(6L)).willReturn(StockInfo.Stock.of(1L, 10));
        given(stockService.getStock(5L)).willReturn(StockInfo.Stock.of(2L, 20));

        ProductResult.Products result = productFacade.getPopularProducts();

        InOrder inOrder = inOrder(paymentService, orderService, productService);
        inOrder.verify(paymentService, times(1)).getCompletedOrdersBetweenDays(3);
        inOrder.verify(orderService, times(1)).getTopPaidProducts(any());
        inOrder.verify(productService, times(1)).getProducts(any());

        assertThat(result.getProducts()).hasSize(2)
                .extracting("productId", "stock")
                .containsExactly(tuple(6L, 10), tuple(5L, 20));
    }

    @DisplayName("판매 중인 상품 목록을 재고와 함께 조회한다.")
    @Test
    void getProducts() {
        given(productService.getSellingProducts())
                .willReturn(ProductInfo.Products.of(List.of(
                        ProductInfo.Product.builder().productId(1L).productName("상품A").productPrice(1000L).build()
                )));
        given(stockService.getStock(1L)).willReturn(StockInfo.Stock.of(1L, 50));

        ProductResult.Products result = productFacade.getProducts();

        assertThat(result.getProducts()).hasSize(1)
                .extracting("productId", "productName", "productPrice", "stock")
                .containsExactly(tuple(1L, "상품A", 1000L, 50));
    }
}
