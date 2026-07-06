package com.github.gokid96.e_commerce.order.application;

import com.github.gokid96.e_commerce.balance.domain.BalanceService;
import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import com.github.gokid96.e_commerce.coupon.domain.CouponService;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.payment.domain.PaymentService;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductService;
import com.github.gokid96.e_commerce.product.domain.stock.StockService;
import com.github.gokid96.e_commerce.rank.domain.RankService;
import com.github.gokid96.e_commerce.user.domain.UserService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderFacadeTest {

    @Mock
    private UserService userService;
    @Mock
    private ProductService productService;
    @Mock
    private CouponService couponService;
    @Mock
    private OrderService orderService;
    @Mock
    private BalanceService balanceService;
    @Mock
    private StockService stockService;
    @Mock
    private PaymentService paymentService;

    @Mock
    private RankService rankService;

    @InjectMocks
    private OrderFacade orderFacade;

    @DisplayName("쿠폰 없이 주문 시 상품조회->주문생성->잔액차감->재고차감->결제->주문완료 순으로 수행된다.")
    @Test
    void createOrderWithoutCoupon() {
        // given
        OrderCriteria.Create criteria = OrderCriteria.Create.of(1L, null,
                List.of(OrderCriteria.OrderProduct.of(10L, 2)));

        given(productService.getOrderProducts(any())).willReturn(
                ProductInfo.OrderProducts.of(List.of(
                        ProductInfo.OrderProduct.of(10L, "상품A", 1000L, 2))));
        given(orderService.createOrder(any())).willReturn(
                OrderInfo.Order.builder().orderId(100L).totalPrice(2000L).discountPrice(0L).build());

        // when
        OrderResult.Order result = orderFacade.createOrder(criteria);

        // then
        assertThat(result.getOrderId()).isEqualTo(100L);

        InOrder inOrder = inOrder(userService, productService, orderService, balanceService, stockService, paymentService, rankService);
        inOrder.verify(userService).getUser(1L);
        inOrder.verify(productService).getOrderProducts(any());
        inOrder.verify(orderService).createOrder(any());
        inOrder.verify(balanceService).useBalance(any());
        inOrder.verify(stockService).deductStock(any());
        inOrder.verify(paymentService).pay(any());
        inOrder.verify(orderService).paidOrder(100L);
        inOrder.verify(rankService).createSellRank(any());

        verify(couponService, never()).getUsableCoupon(any());
        verify(couponService, never()).getCoupon(any());
        verify(couponService, never()).useUserCoupon(any());
    }

    @DisplayName("쿠폰 사용 시 할인율 조회와 쿠폰 사용까지 함께 수행된다.")
    @Test
    void createOrderWithCoupon() {
        // given
        OrderCriteria.Create criteria = OrderCriteria.Create.of(1L, 50L,
                List.of(OrderCriteria.OrderProduct.of(10L, 2)));

        given(productService.getOrderProducts(any())).willReturn(
                ProductInfo.OrderProducts.of(List.of(
                        ProductInfo.OrderProduct.of(10L, "상품A", 1000L, 2))));
        given(couponService.getUsableCoupon(any())).willReturn(
                CouponInfo.UsableCoupon.builder().userCouponId(500L).build());
        given(couponService.getCoupon(any())).willReturn(
                CouponInfo.Coupon.builder().discountRate(0.1).build());
        given(orderService.createOrder(any())).willReturn(
                OrderInfo.Order.builder().orderId(100L).totalPrice(1800L).discountPrice(200L).build());

        // when
        orderFacade.createOrder(criteria);

        // then
        verify(couponService).getUsableCoupon(any());
        verify(couponService).getCoupon(any());
        verify(couponService).useUserCoupon(500L);
        verify(orderService).paidOrder(100L);

    }
}
