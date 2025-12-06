package com.paypay.paymentservice.allocation;

import com.paypay.paymentservice.dto.PaymentResult;
import com.paypay.paymentservice.enums.PaymentType;
import com.paypay.paymentservice.handler.PaymentHandler;
import com.paypay.paymentservice.paymentsource.PaymentSource;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PaymentAllocator {

    private final AllocationStrategy strategy;

    public PaymentResult allocate(long amount, List<PaymentSource> prioritySources) {
        try {
            if (amount <= 0) {
                return PaymentResult.builder()
                        .success(false)
                        .message("INVALID_AMOUNT: Must be > 0")
                        .amountRequested(amount)
                        .amountDebited(0L)
                        .build();
            }

            if (prioritySources == null || prioritySources.isEmpty()) {
                return PaymentResult.builder()
                        .success(false)
                        .message("NO_PAYMENT_SOURCES_PROVIDED")
                        .amountRequested(amount)
                        .amountDebited(0L)
                        .build();
            }

            long totalAvailable = prioritySources.stream()
                    .mapToLong(PaymentSource::getBalance)
                    .sum();

            if (totalAvailable < amount) {
                return PaymentResult.builder()
                        .success(false)
                        .message("INSUFFICIENT_FUNDS: Required=" + amount + "Available=" + totalAvailable)
                        .amountRequested(amount)
                        .amountDebited(0L)
                        .build();
            }

            PaymentHandler head = strategy.buildChain(prioritySources);
            Map<PaymentType, Long> allocations = new LinkedHashMap<>();
            long remaining = head.handle(amount, allocations);
            boolean success = (remaining == 0);

            return PaymentResult.builder()
                    .success(success)
                    .message(success ? "SUCCESS" : "INSUFFICIENT_FUNDS:")
                    .amountRequested(amount)
                    .amountDebited(amount - remaining)
                    .remaining(remaining)
                    .allocations(allocations)
                    .build();

        } catch (Exception e) {
            return PaymentResult.builder()
                    .success(false)
                    .message("ERROR: " + e.getMessage())
                    .amountRequested(amount)
                    .amountDebited(0L)
                    .build();
        }
    }
}
