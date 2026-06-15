package com.github.gokid96.e_commerce.balance.application;

import com.github.gokid96.e_commerce.balance.domain.Balance;
import com.github.gokid96.e_commerce.balance.domain.BalanceRepository;
import com.github.gokid96.e_commerce.support.ConcurrencyTestSupport;
import com.github.gokid96.e_commerce.user.domain.User;
import com.github.gokid96.e_commerce.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class BalanceFacadeConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private BalanceFacade balanceFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @DisplayName("동시성 - 모든 잔액 충전이 정상 처리 되어야 한다.")
    @Test
    void chargeBalanceConcurrency() {
        // given
        User user = User.create("유저1");
        userRepository.save(user);

        Balance balance = Balance.create(user.getId(), 1_000L);
        balanceRepository.save(balance);

        BalanceCriteria.Charge criteria = BalanceCriteria.Charge.of(user.getId(), 1_000L);

        // when
        executeConcurrency(3, () -> balanceFacade.chargeBalance(criteria));

        // then
        Balance expectedBalance = balanceRepository.findOptionalByUserId(user.getId()).orElseThrow();
        assertThat(expectedBalance.getAmount()).isEqualTo(31_000L);


    }

}
