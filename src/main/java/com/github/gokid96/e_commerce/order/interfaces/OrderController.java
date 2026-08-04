package com.github.gokid96.e_commerce.order.interfaces;

import com.github.gokid96.e_commerce.common.ApiResponse;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.order.interfaces.dto.OrderRequest;
import com.github.gokid96.e_commerce.order.interfaces.dto.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse.Order> getOrder(@PathVariable("orderId") Long orderId) {
        OrderInfo.Order order = orderService.getOrder(orderId);
        return ApiResponse.ok(OrderResponse.Order.of(order));
    }

    @PostMapping
    public ApiResponse<Void> createOrder(@Valid @RequestBody OrderRequest request) {
        orderService.createOrder(request.toCommand());
        return ApiResponse.ok();
    }
}