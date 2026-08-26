package com.github.gokid96.e_commerce.balance.interfaces;

import com.github.gokid96.e_commerce.balance.domain.Balance;
import com.github.gokid96.e_commerce.balance.domain.BalanceClient;
import com.github.gokid96.e_commerce.balance.domain.BalanceInfo;
import com.github.gokid96.e_commerce.balance.domain.BalanceRepository;
import com.github.gokid96.e_commerce.balance.support.E2EControllerTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

class BalanceControllerE2ETest extends E2EControllerTestSupport {

    @Autowired
    private BalanceRepository balanceRepository;

    @MockitoBean
    private BalanceClient balanceClient;

    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUpUser() {
        Mockito.when(balanceClient.getUser(USER_ID))
                .thenReturn(BalanceInfo.User.of(USER_ID, "유저"));
    }

    @DisplayName("잔액을 조회한다.")
    @Test
    void getBalance() {
        balanceRepository.save(Balance.create(USER_ID, 100_000L));

        client.get()
                .uri("/api/v1/users/{userId}/balance", USER_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("OK")
                .jsonPath("$.data.amount").isEqualTo(100_000);
    }

    @DisplayName("잔액 충전 시, 최대 잔액을 초과할 수 없다.")
    @Test
    void chargeBalanceWithOverMaxAmount() {
        balanceRepository.save(Balance.create(USER_ID, 10_000_000L));

        client.post()
                .uri("/api/v1/users/{userId}/balance/charge", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("amount", 1L))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("최대 잔액(1,000만원)을 초과할 수 없습니다.");
    }

    @DisplayName("잔액을 충전한다.")
    @Test
    void chargeBalance() {
        balanceRepository.save(Balance.create(USER_ID, 10_000L));

        client.post()
                .uri("/api/v1/users/{userId}/balance/charge", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("amount", 1_000_000L))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("OK");
    }
}
