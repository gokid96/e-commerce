package com.github.gokid96.e_commerce.balance.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private BalanceService balanceService;

    @DisplayName("잔액이 존재하면 충전한다.")
    @Test
    void chargeBalance_existing() {
        // given
        Balance existing = Balance.create(1L, 1_000_000L);
        given(balanceRepository.findOptionalByUserId(1L))
                .willReturn(Optional.of(existing));

        BalanceCommand.Charge command = BalanceCommand.Charge.of(1L, 500_000L);

        // when
        balanceService.chargeBalance(command);

        // then
        assertThat(existing.getAmount()).isEqualTo(1_500_000L);
        verify(balanceRepository, never()).save(any(Balance.class));
    }

    @DisplayName("잔액이 존재하지 않으면 새로 생성해서 저장한다.")
    @Test
    void chargeBalance_new() {
        // given
        given(balanceRepository.findOptionalByUserId(1L))
                .willReturn(Optional.empty());

        BalanceCommand.Charge command = BalanceCommand.Charge.of(1L, 1_000_000L);

        // when
        balanceService.chargeBalance(command);

        // then
        verify(balanceRepository, times(1)).save(any(Balance.class));
    }

    @DisplayName("잔액이 존재하면 사용한다.")
    @Test
    void useBalance() {
        // given
        Balance existing = Balance.create(1L, 1_000_000L);
        given(balanceRepository.findOptionalByUserId(1L))
                .willReturn(Optional.of(existing));

        BalanceCommand.Use command = BalanceCommand.Use.of(1L, 300_000L);

        // when
        balanceService.useBalance(command);

        // then
        assertThat(existing.getAmount()).isEqualTo(700_000L);
    }


    @DisplayName("잔액이 존재하지 않으면 사용 시 예외가 발생한다.")
    @Test
    void useBalance_notFound() {
        //given
        given(balanceRepository.findOptionalByUserId(1L))
                .willReturn(Optional.empty());

        BalanceCommand.Use command = BalanceCommand.Use.of(1L, 300_000L);

        // when & then
        assertThatThrownBy(() -> balanceService.useBalance(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액이 존재하지 않습니다.");
    }

    @DisplayName("잔액이 존재하면 조회한다.")
    @Test
    void getBalance_existing() {
        //given
        Balance existing = Balance.create(1L, 1_000_000L);
        given(balanceRepository.findOptionalByUserId(1L))
                .willReturn(Optional.of(existing));
        //when
        BalanceInfo.Balance info = balanceService.getBalance(1L);
        //than
        assertThat(info.getUserId()).isEqualTo(1L);
        assertThat(info.getAmount()).isEqualTo(1_000_000L);
    }

    @DisplayName("잔액이 존재하지 않으면 0으로 응답한다.")
    @Test
    void getBalance_notFound() {
        // given
        given(balanceRepository.findOptionalByUserId(1L))
                .willReturn(Optional.empty());
        // when
        BalanceInfo.Balance info = balanceService.getBalance(1L);

        // than
        assertThat(info.getUserId()).isEqualTo(1L);
        assertThat(info.getAmount()).isZero();
    }


}
