package com.paypay.paymentservice.paymentsource.impl;

import com.paypay.paymentservice.enums.PaymentType;
import com.paypay.paymentservice.paymentsource.PaymentSource;
import com.paypay.paymentservice.paymentsource.PaymentSourceBehaviour;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoyaltyPoints implements PaymentSource {
    private final PaymentSourceBehaviour behaviour;

    public LoyaltyPoints(long amount) {
        this.behaviour = new PaymentSourceBehaviour(PaymentType.LOYALTY_POINTS, amount);
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
