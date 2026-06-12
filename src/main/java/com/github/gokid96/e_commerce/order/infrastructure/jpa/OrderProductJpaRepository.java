package com.github.gokid96.e_commerce.order.infrastructure.jpa;

import com.github.gokid96.e_commerce.order.domain.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductJpaRepository extends JpaRepository<OrderProduct, Long> {

    List<OrderProduct> findByOrderIdIn(List<Long> orderIds);
}