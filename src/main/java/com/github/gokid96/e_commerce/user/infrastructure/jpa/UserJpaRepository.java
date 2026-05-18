package com.github.gokid96.e_commerce.user.infrastructure.jpa;

import com.github.gokid96.e_commerce.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}