package com.github.gokid96.e_commerce.user.domain;

import com.github.gokid96.e_commerce.user.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class UserServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("사용자를 조회한다.")
    @Test
    void getUser() {
        User user = userRepository.save(User.create("사용자1"));

        UserInfo.User result = userService.getUser(user.getId());

        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getNickname()).isEqualTo("사용자1");
    }

    @DisplayName("존재하지 않는 사용자는 조회할 수 없다.")
    @Test
    void getUserWhenNotFound() {
        assertThatThrownBy(() -> userService.getUser(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자가 존재하지 않습니다.");
    }
}
