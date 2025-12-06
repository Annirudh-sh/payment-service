package com.paypay.paymentservice.paymentsource.impl;

import com.paypay.paymentservice.enums.PaymentType;
import com.paypay.paymentservice.paymentsource.PaymentSource;
import com.paypay.paymentservice.paymentsource.PaymentSourceBehaviour;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreditCard implements PaymentSource {
    private final PaymentSourceBehaviour behaviour;

    public CreditCard(long amount) {
        this.behaviour = new PaymentSourceBehaviour(PaymentType.CREDIT_CARD, amount);
    }

    @Override
    public PaymentType getType() {
        return behaviour.getPaymentType();
    }

    @Override
    public long getBalance() {
        return behaviour.getBalance();
    }

    @Override
    public long debit(long amount) {
        return behaviour.debit(amount);
    }
}
