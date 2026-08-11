package com.github.gokid96.e_commerce.order.infrastructure.jpa;

import com.github.gokid96.e_commerce.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
