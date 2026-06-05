package com.github.gokid96.e_commerce.payment.domain;

import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository {
    Payment save(Payment payment);
}
