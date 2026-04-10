package com.github.gokid96.e_commerce.balance.controller;

import com.github.gokid96.e_commerce.balance.dto.request.BalanceChargeRequest;
import com.github.gokid96.e_commerce.balance.dto.response.BalanceResponse;
import com.github.gokid96.e_commerce.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class BalanceController {

    @GetMapping("/{userId}/balance")
    public ApiResponse<BalanceResponse> getBalance(@PathVariable long userId) {
        return ApiResponse.ok(BalanceResponse.of(1000000L));
    }

    @PostMapping("/{userId}/balance/charge")
    public ApiResponse<?> chargeBalance(@PathVariable long userId, @RequestBody BalanceChargeRequest request) {
        return ApiResponse.ok();
    }
}
