package com.github.gokid96.e_commerce.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private long amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime paidAt;

    @Builder
    private Payment(Long id,Long orderId,long amount, PaymentMethod paymentMethod,
                    PaymentStatus paymentStatus, LocalDateTime paidAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paidAt = paidAt;
    }

    public static Payment create(Long orderId, long amount){
        if(amount <= 0){
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
        return Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .paymentMethod(PaymentMethod.UNKNOWN)
                .paymentStatus(PaymentStatus.READY)
                .build();
    }
    public void pay(){
        if(paymentStatus.cannotPayable()){
            throw new IllegalArgumentException("결제 가능 상태가 아닙니다.");
        }
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }
}



