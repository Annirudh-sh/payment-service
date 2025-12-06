package com.paypay.paymentservice.paymentsource;

import com.paypay.paymentservice.enums.PaymentType;

public interface PaymentSource {
    PaymentType getType();
    long getBalance();
    long debit(long amount);
}
