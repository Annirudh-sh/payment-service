package com.paypay.paymentservice.handler;

import com.paypay.paymentservice.enums.PaymentType;
import com.paypay.paymentservice.paymentsource.PaymentSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class PaymentHandler {

    private final PaymentSource source;

    @Setter
    private PaymentHandler next;

    public long handle(long amount, Map<PaymentType, Long> allocations) {
        if (amount <= 0) return 0L;

        try {
            long used = source.debit(amount);

            if (used < 0) {
                throw new IllegalArgumentException("NEGATIVE_DEBIT from " + source.getType());
            }

            if (used > 0) {
                allocations.merge(source.getType(), used, Long::sum);
                amount -= used;
            }

            if (amount > 0 && next != null) {
                return next.handle(amount, allocations);
            }

            return amount;

        } catch (Exception e) {
            throw new RuntimeException("Failed to process payment for source: " + source.getType() + ": " + e.getMessage());
        }
    }
}
