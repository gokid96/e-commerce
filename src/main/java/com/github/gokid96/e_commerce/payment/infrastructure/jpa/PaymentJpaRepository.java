package com.github.gokid96.e_commerce.payment.infrastructure.jpa;

import com.github.gokid96.e_commerce.payment.domain.Payment;
import com.github.gokid96.e_commerce.payment.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPaymentStatusInAndPaidAtBetween(Collection<PaymentStatus> statuses,
                                                        LocalDateTime paidAtAfter,
                                                        LocalDateTime paidAtBefore);
}
