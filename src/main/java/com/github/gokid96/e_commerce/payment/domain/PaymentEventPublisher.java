package com.github.gokid96.e_commerce.payment.domain;

public interface PaymentEventPublisher {

    void paid(PaymentEvent.Paid event);

    void payFailed(PaymentEvent.PayFailed event);

    void canceled(PaymentEvent.Canceled event);
}