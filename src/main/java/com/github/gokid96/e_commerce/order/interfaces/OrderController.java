package com.github.gokid96.e_commerce.order.interfaces;

import com.github.gokid96.e_commerce.common.ApiResponse;
import com.github.gokid96.e_commerce.order.domain.OrderService;
import com.github.gokid96.e_commerce.order.interfaces.dto.OrderRequest;
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

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<Void> createOrder(@Valid @RequestBody OrderRequest request) {
        orderService.createOrder(request.toCommand());
        return ApiResponse.ok();
    }
}