package com.github.gokid96.e_commerce.payment.infrastructure;

import com.github.gokid96.e_commerce.payment.domain.Payment;
import com.github.gokid96.e_commerce.payment.domain.PaymentRepository;
import com.github.gokid96.e_commerce.payment.domain.PaymentStatus;
import com.github.gokid96.e_commerce.payment.infrastructure.jpa.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentCoreRepository implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment){
        return paymentJpaRepository.save(payment);
    }

    @Override
    public List<Payment> findCompletedPaymentsWithIn(List<PaymentStatus> statuses,
                                                     LocalDateTime startDateTime,
                                                     LocalDateTime endDateTime) {
        return paymentJpaRepository.findByPaymentStatusInAndPaidAtBetween(statuses, startDateTime, endDateTime);
    }
}
