package com.github.gokid96.e_commerce.user.domain;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    User save(User user);
    User findById(Long userId);
}
