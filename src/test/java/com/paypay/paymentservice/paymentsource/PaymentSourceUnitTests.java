package com.paypay.paymentservice.paymentsource;

import com.paypay.paymentservice.enums.PaymentType;
import com.paypay.paymentservice.handler.PaymentHandler;
import com.paypay.paymentservice.paymentsource.impl.LoyaltyPoints;
import com.paypay.paymentservice.paymentsource.impl.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PaymentSourceUnitTests {

    @Test
    void loyaltyPointsDebit_partial() {
        PaymentSource points = new LoyaltyPoints(3000);
        long deducted = points.debit(2000);
        assertEquals(2000, deducted);
        assertEquals(1000, points.getBalance());
    }

    @Test
    void loyaltyPointsDebit_full() {
        PaymentSource points = new LoyaltyPoints(3000);
        long deducted = points.debit(5000);
        assertEquals(3000, deducted);
        assertEquals(0, points.getBalance());
    }

    @Test
    void handler_partialPayment() {
        PaymentSource points = new LoyaltyPoints(3000);
        PaymentSource wallet = new Wallet(4000);

        PaymentHandler pointsHandler = new PaymentHandler(points);
        PaymentHandler walletHandler = new PaymentHandler(wallet);
        pointsHandler.setNext(walletHandler);

        HashMap<PaymentType, Long> allocations = new HashMap<>();
        long remaining = pointsHandler.handle(5000, allocations);

        assertEquals(0, remaining);
        assertEquals(2, allocations.size());
        assertEquals(3000, allocations.get(PaymentType.LOYALTY_POINTS));
        assertEquals(2000, allocations.get(PaymentType.WALLET));
    }


    @Test
    void handle_noFunds() {
        PaymentSource points = new LoyaltyPoints(0);
        PaymentHandler handler = new PaymentHandler(points);

        HashMap<PaymentType, Long> allocations = new HashMap<>();
        long remaining = handler.handle(1000, allocations);

        assertEquals(1000, remaining);
    }
}
