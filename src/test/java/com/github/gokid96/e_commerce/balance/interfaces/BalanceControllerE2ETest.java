package com.github.gokid96.e_commerce.balance.interfaces;

import com.github.gokid96.e_commerce.balance.domain.Balance;
import com.github.gokid96.e_commerce.balance.domain.BalanceRepository;
import com.github.gokid96.e_commerce.support.E2EControllerTestSupport;
import com.github.gokid96.e_commerce.user.domain.User;
import com.github.gokid96.e_commerce.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Map;

class BalanceControllerE2ETest extends E2EControllerTestSupport {

    @Autowired private UserRepository userRepository;
    @Autowired private BalanceRepository balanceRepository;

    private User user;

    @BeforeEach
    void setUpUser() {
        user = userRepository.save(User.create("유저"));
    }

    @DisplayName("잔액을 조회한다.")
    @Test
    void getBalance() {
        balanceRepository.save(Balance.create(user.getId(), 100_000L));

        client.get()
                .uri("/api/v1/users/{userId}/balance", user.getId())
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
        balanceRepository.save(Balance.create(user.getId(), 10_000_000L));

        client.post()
                .uri("/api/v1/users/{userId}/balance/charge", user.getId())
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
        balanceRepository.save(Balance.create(user.getId(), 10_000L));

        client.post()
                .uri("/api/v1/users/{userId}/balance/charge", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("amount", 1_000_000L))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("OK");
    }
}