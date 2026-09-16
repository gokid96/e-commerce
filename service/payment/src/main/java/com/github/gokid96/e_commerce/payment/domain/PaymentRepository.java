package com.github.gokid96.e_commerce.payment.domain;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    Optional<Payment> findByOrderId(Long orderId);
}