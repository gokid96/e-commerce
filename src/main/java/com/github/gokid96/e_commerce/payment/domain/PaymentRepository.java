package com.github.gokid96.e_commerce.payment.domain;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository {
    Payment save(Payment payment);

    List<Payment> findCompletedPaymentsWithIn(List<PaymentStatus> statuses,
                                              LocalDateTime startDateTime,
                                              LocalDateTime endDateTime);
}
