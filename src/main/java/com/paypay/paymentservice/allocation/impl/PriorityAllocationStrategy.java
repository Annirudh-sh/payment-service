package com.paypay.paymentservice.allocation.impl;

import com.paypay.paymentservice.allocation.AllocationStrategy;
import com.paypay.paymentservice.handler.PaymentHandler;
import com.paypay.paymentservice.paymentsource.PaymentSource;

import java.util.List;

public class PriorityAllocationStrategy implements AllocationStrategy {
    @Override
    public PaymentHandler buildChain(List<PaymentSource> priorityOrderSources) {

        if (priorityOrderSources == null || priorityOrderSources.isEmpty()) {
            throw new IllegalArgumentException("At least one payment source is required to build chain");
        }

        PaymentHandler head = null, prev = null;

        for (PaymentSource s: priorityOrderSources) {
            PaymentHandler handler = new PaymentHandler(s);
            if (head == null) head = handler;
            if (prev != null) prev.setNext(handler);
            prev = handler;
        }

        return head;
    }
}
