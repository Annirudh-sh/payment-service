package com.paypay.paymentservice.paymentsource;

import com.paypay.paymentservice.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentSourceBehaviour {
    private final PaymentType paymentType;
    private long balance;

    public long debit(long amount) {
        long deducted = Math.min(balance, amount);
        balance -= deducted;
        return deducted;
    }
}
