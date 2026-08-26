package com.github.gokid96.e_commerce.user.interfaces;

import com.github.gokid96.e_commerce.user.domain.UserInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    @Getter
    @NoArgsConstructor
    public static class User {

        private Long userId;
        private String nickname;

        private User(Long userId, String nickname) {
            this.userId = userId;
            this.nickname = nickname;
        }

        public static User of(UserInfo.User user) {
            return new User(user.getUserId(), user.getNickname());
        }
    }
}
