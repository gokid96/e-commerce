package com.github.gokid96.e_commerce.balance.domain;

import com.github.gokid96.e_commerce.balance.infrastructure.jpa.BalanceTransactionJpaRepository;
import com.github.gokid96.e_commerce.balance.support.IntegrationTestSupport;
import com.github.gokid96.e_commerce.balance.support.database.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class BalanceServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired private BalanceService balanceService;
    @Autowired private BalanceRepository balanceRepository;
    @Autowired private BalanceTransactionJpaRepository balanceTransactionJpaRepository;
    @Autowired private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.clean();
    }

    @DisplayName("잔액이 존재하면 충전 금액을 더해 저장한다.")
    @Test
    void chargeBalanceWhenExists() {
        balanceRepository.save(Balance.create(1L, 10_000L));

        balanceService.chargeBalance(BalanceCommand.Charge.of(1L, 5_000L));

        assertThat(balanceRepository.findOptionalByUserId(1L).orElseThrow().getAmount())
                .isEqualTo(15_000L);
    }

    @DisplayName("잔액이 없으면 새로 생성해 저장한다.")
    @Test
    void chargeBalanceWhenNotExists() {
        balanceService.chargeBalance(BalanceCommand.Charge.of(1L, 5_000L));

        assertThat(balanceRepository.findOptionalByUserId(1L).orElseThrow().getAmount())
                .isEqualTo(5_000L);
    }

    @DisplayName("최대 잔액을 초과하면 충전할 수 없다.")
    @Test
    void chargeBalanceWhenExceedsMax() {
        balanceRepository.save(Balance.create(1L, 10_000_000L));

        assertThatThrownBy(() -> balanceService.chargeBalance(BalanceCommand.Charge.of(1L, 1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 잔액(1,000만원)을 초과할 수 없습니다.");
    }

    @DisplayName("잔액을 사용하면 사용 금액만큼 차감된다.")
    @Test
    void useBalance() {
        balanceRepository.save(Balance.create(1L, 10_000L));

        balanceService.useBalance(BalanceCommand.Use.of(1L, 4_000L));

        assertThat(balanceRepository.findOptionalByUserId(1L).orElseThrow().getAmount())
                .isEqualTo(6_000L);
    }

    @DisplayName("잔액이 부족하면 사용할 수 없다.")
    @Test
    void useBalanceWhenInsufficient() {
        balanceRepository.save(Balance.create(1L, 3_000L));

        assertThatThrownBy(() -> balanceService.useBalance(BalanceCommand.Use.of(1L, 3_001L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액이 부족합니다.");
    }

    @DisplayName("잔액이 없으면 사용할 수 없다.")
    @Test
    void useBalanceWhenNotExists() {
        assertThatThrownBy(() -> balanceService.useBalance(BalanceCommand.Use.of(1L, 1_000L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액이 존재하지 않습니다.");
    }

    @DisplayName("잔액을 환불하면 환불 금액만큼 증가한다.")
    @Test
    void refundBalance() {
        balanceRepository.save(Balance.create(1L, 6_000L));

        balanceService.refundBalance(BalanceCommand.Refund.of(1L, 4_000L));

        assertThat(balanceRepository.findOptionalByUserId(1L).orElseThrow().getAmount())
                .isEqualTo(10_000L);
    }

    @DisplayName("잔액이 없으면 환불할 수 없다.")
    @Test
    void refundBalanceWhenNotExists() {
        assertThatThrownBy(() -> balanceService.refundBalance(BalanceCommand.Refund.of(1L, 1_000L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액이 존재하지 않습니다.");
    }

    @DisplayName("충전·사용·환불 내역이 부호와 유형까지 순서대로 기록된다.")
    @Test
    void saveTransactionHistory() {
        balanceService.chargeBalance(BalanceCommand.Charge.of(1L, 10_000L));
        balanceService.useBalance(BalanceCommand.Use.of(1L, 3_000L));
        balanceService.refundBalance(BalanceCommand.Refund.of(1L, 1_000L));

        // 사용 내역은 음수로 기록되어야 한다. 단위 테스트의 verify(any()) 로는 확인되지 않는 부분이다.
        List<BalanceTransaction> transactions = balanceTransactionJpaRepository.findAll();
        assertThat(transactions).hasSize(3)
                .extracting("amount", "type")
                .containsExactly(
                        tuple(10_000L, BalanceTransactionType.CHARGE),
                        tuple(-3_000L, BalanceTransactionType.USE),
                        tuple(1_000L, BalanceTransactionType.REFUND)
                );
    }

    @DisplayName("잔액이 없는 사용자를 조회하면 0원으로 응답한다.")
    @Test
    void getBalanceWhenNotExists() {
        BalanceInfo.Balance info = balanceService.getBalance(1L);

        assertThat(info.getUserId()).isEqualTo(1L);
        assertThat(info.getAmount()).isZero();
    }

    @DisplayName("잔액을 조회한다.")
    @Test
    void getBalance() {
        balanceRepository.save(Balance.create(1L, 7_000L));

        BalanceInfo.Balance info = balanceService.getBalance(1L);

        assertThat(info.getAmount()).isEqualTo(7_000L);
    }
}
