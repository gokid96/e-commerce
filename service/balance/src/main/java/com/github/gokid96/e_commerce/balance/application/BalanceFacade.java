package com.github.gokid96.e_commerce.balance.application;
import com.github.gokid96.e_commerce.balance.domain.BalanceClient;
import com.github.gokid96.e_commerce.balance.domain.BalanceCommand;
import com.github.gokid96.e_commerce.balance.domain.BalanceInfo;
import com.github.gokid96.e_commerce.balance.domain.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceFacade {
    private final BalanceClient balanceClient;
    private final BalanceService balanceService;

    public void chargeBalance(BalanceCriteria.Charge criteria) {
        balanceClient.getUser(criteria.getUserId());        // 존재 검증
        balanceService.chargeBalance(criteria.toCommand()); // 충전
    }

    public BalanceResult.Balance getBalance(Long userId){
        balanceClient.getUser(userId);                                // 존재 검증
        BalanceInfo.Balance info = balanceService.getBalance(userId); // 조회
        return BalanceResult.Balance.of(info);
    }

    public void useBalance(Long userId, Long amount) {
        balanceService.useBalance(BalanceCommand.Use.of(userId, amount));
    }

    public void refundBalance(Long userId, Long amount) {
        balanceService.refundBalance(BalanceCommand.Refund.of(userId, amount));
    }

}
