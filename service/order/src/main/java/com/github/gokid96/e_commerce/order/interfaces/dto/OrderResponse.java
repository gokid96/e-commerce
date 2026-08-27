package com.github.gokid96.e_commerce.order.interfaces.dto;

import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.domain.OrderStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {

    @Getter
    public static class Order {
        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final OrderStatus status;

        private Order(Long orderId, Long userId, Long userCouponId,
                      long totalPrice, long discountPrice, OrderStatus status) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
            this.status = status;
        }

        public static Order of(OrderInfo.Order order) {
            return new Order(
                    order.getOrderId(),
                    order.getUserId(),
                    order.getUserCouponId(),
                    order.getTotalPrice(),
                    order.getDiscountPrice(),
                    order.getStatus());
        }
    }
}