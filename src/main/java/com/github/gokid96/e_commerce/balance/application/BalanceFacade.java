package com.github.gokid96.e_commerce.balance.application;
import com.github.gokid96.e_commerce.balance.domain.BalanceInfo;
import com.github.gokid96.e_commerce.balance.domain.BalanceService;
import com.github.gokid96.e_commerce.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceFacade {
    private final UserService userService;
    private final BalanceService balanceService;

    public void chargeBalance(BalanceCriteria.Charge criteria) {
        userService.getUser(criteria.getUserId());          // 존재 검증
        balanceService.chargeBalance(criteria.toCommand()); // 충전
    }

    public BalanceResult.Balance getBalance(Long userId){
        userService.getUser(userId);                                  // 존재 검증
        BalanceInfo.Balance info = balanceService.getBalance(userId); // 조회
        return BalanceResult.Balance.of(info);
    }


}
