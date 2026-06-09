package com.github.gokid96.e_commerce.order.interfaces;

import com.github.gokid96.e_commerce.common.ApiResponse;
import com.github.gokid96.e_commerce.order.application.OrderFacade;
import com.github.gokid96.e_commerce.order.application.OrderResult;
import com.github.gokid96.e_commerce.order.interfaces.dto.OrderRequest;
import com.github.gokid96.e_commerce.order.interfaces.dto.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    @PostMapping
    public ApiResponse<OrderResponse.Order> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResult.Order result = orderFacade.createOrder(request.toCriteria());
        return ApiResponse.ok(OrderResponse.Order.of(result));
    }
}