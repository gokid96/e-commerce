package com.github.gokid96.e_commerce.balance.infrastructure;

import com.github.gokid96.e_commerce.balance.domain.BalanceClient;
import com.github.gokid96.e_commerce.balance.domain.BalanceInfo;
import com.github.gokid96.e_commerce.common.client.api.user.UserApiClient;
import com.github.gokid96.e_commerce.common.client.api.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceCoreClient implements BalanceClient {

    private final UserApiClient userApiClient;

    @Override
    public BalanceInfo.User getUser(Long userId) {
        UserResponse.User user = userApiClient.getUser(userId).getData();
        return BalanceInfo.User.of(user.getUserId(), user.getNickname());
    }
}
