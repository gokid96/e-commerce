package com.github.gokid96.e_commerce.order.infrastructure;

import com.github.gokid96.e_commerce.common.client.api.ApiResponse;
import com.github.gokid96.e_commerce.common.client.api.coupon.CouponApiClient;
import com.github.gokid96.e_commerce.common.client.api.product.ProductApiClient;
import com.github.gokid96.e_commerce.common.client.api.product.ProductRequest;
import com.github.gokid96.e_commerce.common.client.api.product.ProductResponse;
import com.github.gokid96.e_commerce.order.domain.OrderCommand;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OrderCoreClientTest extends MockTestSupport {

    private static final ObjectMapper MAPPER = JsonMapper.builder().build();

    @Mock
    private ProductApiClient productApiClient;

    @Mock
    private CouponApiClient couponApiClient;

    @InjectMocks
    private OrderCoreClient orderCoreClient;

    @DisplayName("주문 상품 조회 결과에는 재고가 아니라 요청 수량이 담긴다.")
    @Test
    void getProductsUsesRequestedQuantity() {
        // given - 재고는 100개인데 주문 요청은 2개
        givenProduct(1L, "상품A", 10_000L, 100);

        List<OrderCommand.OrderProduct> command = List.of(OrderCommand.OrderProduct.of(1L, 2));

        // when
        List<OrderInfo.Product> products = orderCoreClient.getProducts(command);

        // then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getQuantity()).isEqualTo(2);
        assertThat(products.get(0).getPrice()).isEqualTo(10_000L);
    }

    @DisplayName("같은 상품을 여러 줄로 주문하면 수량이 합산된다.")
    @Test
    void getProductsMergesDuplicatedProduct() {
        // given
        givenProduct(1L, "상품A", 10_000L, 100);

        List<OrderCommand.OrderProduct> command = List.of(
                OrderCommand.OrderProduct.of(1L, 2),
                OrderCommand.OrderProduct.of(1L, 3)
        );

        // when
        List<OrderInfo.Product> products = orderCoreClient.getProducts(command);

        // then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getQuantity()).isEqualTo(5);
    }

    @SuppressWarnings("unchecked")
    private void givenProduct(Long productId, String name, long price, int stock) {
        String json = """
                {
                  "products": [
                    {"productId": %d, "productName": "%s", "productPrice": %d, "stock": %d}
                  ]
                }
                """.formatted(productId, name, price, stock);

        ProductResponse.Products data = MAPPER.readValue(json, ProductResponse.Products.class);

        ApiResponse<ProductResponse.Products> response = mock(ApiResponse.class);
        given(response.getData()).willReturn(data);
        given(productApiClient.getProducts(any(ProductRequest.Products.class))).willReturn(response);
    }
}