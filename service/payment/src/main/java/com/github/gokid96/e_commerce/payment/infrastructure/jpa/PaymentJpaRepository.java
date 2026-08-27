package com.github.gokid96.e_commerce.payment.infrastructure.jpa;

import com.github.gokid96.e_commerce.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
}