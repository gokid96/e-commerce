package com.github.gokid96.e_commerce.order.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderInfo {

    @Getter
    public static class Order {
        private final Long orderId;
        private final long totalPrice;
        private final long discountPrice;

        @Builder
        private Order(Long orderId, long totalPrice, long discountPrice) {
            this.orderId = orderId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
        }

        public static Order of(com.github.gokid96.e_commerce.order.domain.Order order) {
            return Order.builder()
                    .orderId(order.getId())
                    .totalPrice(order.getTotalPrice())
                    .discountPrice(order.getDiscountPrice())
                    .build();
        }
    }

    @Getter
    public static class PaidProduct {
        private final Long productId;
        private final int quantity;

        public PaidProduct(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static PaidProduct of(Long productId, int quantity) {
            return new PaidProduct(productId, quantity);
        }
    }

    @Getter
    public static class PaidProducts {
        private final List<PaidProduct> products;


        @Builder
        private PaidProducts(List<PaidProduct> products) {
            this.products = products;
        }

        public static PaidProducts of(List<PaidProduct> products) {
            return PaidProducts.builder().products(products).build();
        }
    }

}
