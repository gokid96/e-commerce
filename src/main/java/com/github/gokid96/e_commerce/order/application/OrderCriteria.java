package com.github.gokid96.e_commerce.order.application;

import com.github.gokid96.e_commerce.balance.domain.BalanceCommand;
import com.github.gokid96.e_commerce.coupon.domain.CouponCommand;
import com.github.gokid96.e_commerce.order.domain.OrderCommand;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.payment.domain.PaymentCommand;
import com.github.gokid96.e_commerce.product.domain.product.ProductCommand;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.stock.StockCommand;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class OrderCriteria {

    @Getter
    public static class Create {
        private final Long userId;
        private final Long couponId;
        private final List<OrderProduct> products;

        @Builder
        private Create(Long userId, Long couponId, List<OrderProduct> products) {
            this.userId = userId;
            this.couponId = couponId;
            this.products = products;
        }

        public static Create of(Long userId, Long couponId, List<OrderProduct> products) {
            return Create.builder()
                    .userId(userId)
                    .couponId(couponId)
                    .products(products)
                    .build();
        }

        public ProductCommand.OrderProducts toProductCommand() {
            return ProductCommand.OrderProducts.of(
                    products.stream()
                            .map(p -> ProductCommand.OrderProduct.of(p.getProductId(), p.getQuantity()))
                            .toList()
            );
        }

        public StockCommand.OrderProducts toStockCommand() {
            return StockCommand.OrderProducts.of(
                    products.stream()
                            .map(p -> StockCommand.OrderProduct.of(p.getProductId(), p.getQuantity()))
                            .toList()
            );
        }

        public CouponCommand.UsableCoupon toCouponCommand() {
            return CouponCommand.UsableCoupon.of(userId, couponId);
        }

        public BalanceCommand.Use toBalanceCommand(long amount) {
            return BalanceCommand.Use.of(userId, amount);
        }

        public OrderCommand.Create toOrderCommand(Long userCouponId, double discountRate, ProductInfo.OrderProducts orderProducts) {
            return OrderCommand.Create.of(
                    userId,
                    userCouponId,
                    discountRate,
                    orderProducts.getProducts().stream()
                            .map(p -> OrderCommand.OrderProduct.of(
                                    p.getProductId(), p.getProductName(), p.getProductPrice(), p.getQuantity()))
                            .toList()
            );
        }

        public PaymentCommand.Payment toPaymentCommand(OrderInfo.Order order) {
            return PaymentCommand.Payment.of(order.getOrderId(), order.getTotalPrice());
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
                    .quantity(quantity)
                    .build();
        }
    }
}