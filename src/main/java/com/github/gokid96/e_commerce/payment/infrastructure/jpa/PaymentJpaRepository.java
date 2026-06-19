package com.github.gokid96.e_commerce.payment.infrastructure.jpa;

import com.github.gokid96.e_commerce.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
