package com.github.gokid96.e_commerce.order.controller;

import com.github.gokid96.e_commerce.common.ApiResponse;
import com.github.gokid96.e_commerce.order.dto.request.OrderRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @PostMapping
    public ApiResponse<?> CreateOrder(@RequestBody OrderRequest request) {
        return ApiResponse.ok();
    }
}
