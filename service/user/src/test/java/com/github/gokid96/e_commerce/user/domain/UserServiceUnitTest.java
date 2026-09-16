package com.github.gokid96.e_commerce.user.domain;

import com.github.gokid96.e_commerce.user.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class UserServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("사용자를 조회한다.")
    @Test
    void getUser() {
        given(userRepository.findById(1L))
                .willReturn(User.builder().id(1L).nickname("사용자1").build());

        UserInfo.User result = userService.getUser(1L);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getNickname()).isEqualTo("사용자1");
    }

    @DisplayName("존재하지 않는 사용자는 조회할 수 없다.")
    @Test
    void getUserWhenNotFound() {
        given(userRepository.findById(999L))
                .willThrow(new IllegalArgumentException("사용자가 존재하지 않습니다."));

        assertThatThrownBy(() -> userService.getUser(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자가 존재하지 않습니다.");
    }
}
