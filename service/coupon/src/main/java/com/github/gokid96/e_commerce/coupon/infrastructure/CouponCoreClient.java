package com.github.gokid96.e_commerce.coupon.infrastructure;

import com.github.gokid96.e_commerce.common.client.api.user.UserApiClient;
import com.github.gokid96.e_commerce.common.client.api.user.UserResponse;
import com.github.gokid96.e_commerce.coupon.domain.CouponClient;
import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponCoreClient implements CouponClient {

    private final UserApiClient userApiClient;

    @Override
    public CouponInfo.User getUser(Long userId) {
        UserResponse.User user = userApiClient.getUser(userId).getData();
        return CouponInfo.User.of(user.getUserId(), user.getNickname());
    }
}
