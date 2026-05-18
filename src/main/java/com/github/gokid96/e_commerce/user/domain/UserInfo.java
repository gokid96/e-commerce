package com.github.gokid96.e_commerce.user.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfo {
    @Getter
    public static  class User {
        private final  Long userId;
        private final  String userName;

        @Builder
        private User(Long userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

    }


}
