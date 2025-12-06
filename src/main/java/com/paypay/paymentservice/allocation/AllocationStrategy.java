package com.paypay.paymentservice.allocation;

import com.paypay.paymentservice.handler.PaymentHandler;
import com.paypay.paymentservice.paymentsource.PaymentSource;

import java.util.List;

public interface AllocationStrategy {
    PaymentHandler buildChain(List<PaymentSource> priorityOrderSources);
}
