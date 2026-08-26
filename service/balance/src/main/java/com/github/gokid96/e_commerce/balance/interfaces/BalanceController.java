package com.github.gokid96.e_commerce.balance.interfaces;

import com.github.gokid96.e_commerce.balance.application.BalanceFacade;
import com.github.gokid96.e_commerce.balance.application.BalanceResult;
import com.github.gokid96.e_commerce.balance.interfaces.dto.BalanceRequest;
import com.github.gokid96.e_commerce.balance.interfaces.dto.BalanceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceFacade balanceFacade;

    @GetMapping("/{userId}/balance")
    public ApiResponse<BalanceResponse.Balance> getBalance(@PathVariable("userId") Long userId) {
        BalanceResult.Balance result = balanceFacade.getBalance(userId);
        return ApiResponse.ok(BalanceResponse.Balance.of(result));
    }

    @PostMapping("/{userId}/balance/charge")
    public ApiResponse<Void> charge(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody BalanceRequest.Charge request) {
        balanceFacade.chargeBalance(request.toCriteria(userId));
        return ApiResponse.ok();
    }

    @PostMapping("/{userId}/balance/use")
    public ApiResponse<Void> use(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody BalanceRequest.Use request) {
        balanceFacade.useBalance(userId, request.getAmount());
        return ApiResponse.ok();
    }

    @PostMapping("/{userId}/balance/refund")
    public ApiResponse<Void> refund(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody BalanceRequest.Refund request) {
        balanceFacade.refundBalance(userId, request.getAmount());
        return ApiResponse.ok();
    }

}
