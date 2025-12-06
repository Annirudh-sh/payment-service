package com.paypay.paymentservice.dto;

import com.paypay.paymentservice.enums.PaymentType;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Builder
public record PaymentResult(
        boolean success,
        String message,
        long amountRequested,
        long amountDebited,
        long remaining,
        Map<PaymentType, Long> allocations
) {

    public void printSummary() {
        log.info("==== Payment Result ====");
        log.info("Success:    {}", success);
        log.info("Status:    {}", message);
        log.info("Requested: {}", amountRequested);
        log.info("Paid:      {}", amountDebited);
        log.info("Remaining: {}", remaining);
        log.info("Breakdown: ");
        allocations.forEach((k, v) -> log.info("{}: {}", k, v));
        log.info("=======================");
    }
}
