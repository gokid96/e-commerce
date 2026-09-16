package com.github.gokid96.e_commerce.product.domain.product;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCommand {

    @Getter
    public static class OrderProducts {
        private final List<OrderProduct> products;

        @Builder
        private OrderProducts(List<OrderProduct> products) {
            this.products = products;
        }

        public static OrderProducts of(List<OrderProduct> products) {
            return OrderProducts.builder().products(products).build();
        }
    }

    @Getter
    public static class OrderProduct {
        private final Long productId;
        private final int quantity;

        @Builder
        private OrderProduct(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, int quantity) {
            return OrderProduct.builder()
                    .productId(productId)
                    .quantity(quantity).build();
        }
    }

    /**
     * 상품 조회 조건. 커서 페이징과 ID 목록 조회를 같은 쿼리로 처리한다.
     * {@code statuses} 가 비어 있으면 판매 상태로 걸러내지 않는다.
     */
    @Getter
    public static class Query {

        private final Long pageSize;
        private final Long cursor;
        private final List<Long> ids;
        private final List<ProductSellingStatus> statuses;

        private Query(Long pageSize, Long cursor, List<Long> ids, List<ProductSellingStatus> statuses) {
            this.pageSize = pageSize;
            this.cursor = cursor;
            this.ids = ids;
            this.statuses = statuses;
        }

        /** 판매 중 상품의 커서 페이징 조회. */
        public static Query ofSelling(Long pageSize, Long cursor) {
            return new Query(pageSize, cursor, List.of(), ProductSellingStatus.forSelling());
        }

        /**
         * ID 목록 조회. 주문 등 내부 호출용이므로 판매 상태로 걸러내지 않는다.
         * (판매 중지 상품이 조용히 누락되면 주문 금액이 어긋나므로, 누락은 조회 측에서 예외로 처리한다.)
         */
        public static Query ofIds(List<Long> ids) {
            return new Query((long) ids.size(), null, ids, List.of());
        }
    }
}
