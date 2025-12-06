package com.paypay.paymentservice;

import com.paypay.paymentservice.allocation.PaymentAllocator;
import com.paypay.paymentservice.allocation.impl.PriorityAllocationStrategy;
import com.paypay.paymentservice.dto.PaymentResult;
import com.paypay.paymentservice.enums.PaymentType;
import com.paypay.paymentservice.paymentsource.PaymentSource;
import com.paypay.paymentservice.paymentsource.impl.CreditCard;
import com.paypay.paymentservice.paymentsource.impl.LoyaltyPoints;
import com.paypay.paymentservice.paymentsource.impl.VirtualCard;
import com.paypay.paymentservice.paymentsource.impl.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceApplicationTests {

    @Test
    void contextLoads() {
    }
    
    @Test
    void allocate_fullPayment_happyPath() {
        long amountToPay = 10000;

        PaymentSource points = new LoyaltyPoints(3000);
        PaymentSource wallet = new Wallet(4000);
        PaymentSource vc = new VirtualCard(2000);
        PaymentSource cc = new CreditCard(5000);

        List<PaymentSource> prioritySources = List.of(points, wallet, vc, cc);

        PaymentResult result = getPaymentResult(amountToPay, prioritySources);

        assertTrue(result.success());
        assertEquals(10000, result.amountDebited());
        assertEquals(0, result.remaining());
        assertEquals(3000, result.allocations().get(PaymentType.LOYALTY_POINTS));
        assertEquals(4000, result.allocations().get(PaymentType.WALLET));
        assertEquals(2000, result.allocations().get(PaymentType.VIRTUAL_CARD));
        assertEquals(1000, result.allocations().get(PaymentType.CREDIT_CARD));
    }

    @Test
    void allocate_insufficientFunds() {
        long amountToPay = 10001;

        PaymentSource points = new LoyaltyPoints(3000);
        PaymentSource wallet = new Wallet(4000);
        PaymentSource vc = new VirtualCard(2000);
        PaymentSource cc = new CreditCard(1000);

        List<PaymentSource> prioritySources = List.of(points, wallet, vc, cc);

        PaymentResult result = getPaymentResult(amountToPay, prioritySources);

        assertFalse(result.success());
        assertEquals("INSUFFICIENT_FUNDS: Required=10001 Available=10000", result.message());
        assertEquals(0, result.amountDebited());
        assertEquals(10001, result.amountRequested());
    }

    @Test
    void allocate_partialPayment_withCreditCard() {
        long amountToPay = 12000;

        PaymentSource points = new LoyaltyPoints(3000);
        PaymentSource wallet = new Wallet(4000);
        PaymentSource vc = new VirtualCard(2000);
        PaymentSource cc = new CreditCard(5000);

        List<PaymentSource> prioritySources = List.of(points, wallet, vc, cc);

        PaymentResult result = getPaymentResult(amountToPay, prioritySources);

        assertTrue(result.success());
        assertEquals(12000, result.amountDebited());
        assertEquals(0, result.remaining());
        assertEquals(3000, result.allocations().get(PaymentType.LOYALTY_POINTS));
        assertEquals(4000, result.allocations().get(PaymentType.WALLET));
        assertEquals(2000, result.allocations().get(PaymentType.VIRTUAL_CARD));
        assertEquals(3000, result.allocations().get(PaymentType.CREDIT_CARD));
    }

    @Test
    void allocate_invalidAmount() {
        long amountToPay = -1;

        PaymentSource points = new LoyaltyPoints(3000);

        List<PaymentSource> prioritySources = List.of(points);

        PaymentResult result = getPaymentResult(amountToPay, prioritySources);

        assertFalse(result.success());
        assertEquals("INVALID_AMOUNT: Must be > 0", result.message());
        assertEquals(0, result.amountDebited());
        assertEquals(-1, result.amountRequested());
    }

    private static PaymentResult getPaymentResult(long amountToPay, List<PaymentSource> prioritySources) {
        PaymentAllocator allocator = new PaymentAllocator(new PriorityAllocationStrategy());
        return allocator.allocate(amountToPay, prioritySources);
    }
}
