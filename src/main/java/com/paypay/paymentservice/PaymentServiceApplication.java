package com.paypay.paymentservice;

import com.paypay.paymentservice.allocation.AllocationStrategy;
import com.paypay.paymentservice.allocation.PaymentAllocator;
import com.paypay.paymentservice.allocation.impl.PriorityAllocationStrategy;
import com.paypay.paymentservice.dto.PaymentResult;
import com.paypay.paymentservice.enums.PaymentType;
import com.paypay.paymentservice.factory.PaymentSourceFactory;
import com.paypay.paymentservice.paymentsource.PaymentSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);

        long amountToPay = 10_000L;

        PaymentSource points = PaymentSourceFactory.create(PaymentType.WALLET, 3_000L);
        PaymentSource wallet = PaymentSourceFactory.create(PaymentType.WALLET, 4_000L);
        PaymentSource cc = PaymentSourceFactory.create(PaymentType.CREDIT_CARD, 3_000L);
        PaymentSource vc = PaymentSourceFactory.create(PaymentType.VIRTUAL_CARD, 2_000L);

        List<PaymentSource> priority = Arrays.asList(points, wallet, vc, cc);
        AllocationStrategy strategy = new PriorityAllocationStrategy();
        PaymentAllocator allocator = new PaymentAllocator(strategy);

        PaymentResult result = allocator.allocate(amountToPay, priority);
        result.printSummary();

        for (PaymentSource ps: priority) {
            log.info(" {} -> {}", ps.getType(), ps.getBalance());
        }
    }

}
