package com.github.gokid96.e_commerce.user.interfaces;

import com.github.gokid96.e_commerce.user.domain.UserInfo;
import com.github.gokid96.e_commerce.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/v1/users/{id}")
    public ApiResponse<UserResponse.User> getUser(@PathVariable("id") Long id) {
        UserInfo.User user = userService.getUser(id);
        return ApiResponse.ok(UserResponse.User.of(user));
    }
}
