package com.github.gokid96.e_commerce.common.client.api.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponse {

    @Getter
    @NoArgsConstructor
    public static class User {
        private Long userId;
        private String nickname;
    }
}
