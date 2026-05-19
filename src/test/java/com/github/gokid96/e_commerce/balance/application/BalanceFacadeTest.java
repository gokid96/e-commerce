package com.github.gokid96.e_commerce.balance.application;

import com.github.gokid96.e_commerce.balance.domain.BalanceCommand;
import com.github.gokid96.e_commerce.balance.domain.BalanceInfo;
import com.github.gokid96.e_commerce.balance.domain.BalanceService;
import com.github.gokid96.e_commerce.user.domain.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class BalanceFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private BalanceFacade balanceFacade;

    @DisplayName("잔액 충전 시 사용자 검증 후 잔액 충전이 순서대로 수행된다.")
    @Test
    void chargeBalance(){
        // given
        BalanceCriteria.Charge criteria = BalanceCriteria.Charge.of(1L,1_000_000L);

        // when
        balanceFacade.chargeBalance(criteria);

        // then
        InOrder inOrder = inOrder(userService,balanceService);
        inOrder.verify(userService).getUser(1L);
        inOrder.verify(balanceService).chargeBalance(any(BalanceCommand.Charge.class));
    }

    @DisplayName("잔액 조회시 사용자 검증 후 잔액 조회가 순서대로 수행된다.")
    @Test
    void getBalance(){
        // given
        BalanceInfo.Balance info = BalanceInfo.Balance.builder()
                .userId(1L)
                .amount(1_000_000L)
                .build();
        given(balanceService.getBalance(1L)).willReturn(info);

        // when
        BalanceResult.Balance result = balanceFacade.getBalance(1L);

        // then
        assertThat(result.getAmount()).isEqualTo(1_000_000L);

        InOrder inOder = inOrder(userService, balanceService);
        inOder.verify(userService).getUser(1L);
        inOder.verify(balanceService).getBalance(1L);

    }


}
