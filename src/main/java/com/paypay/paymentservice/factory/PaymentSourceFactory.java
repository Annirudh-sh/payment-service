package com.paypay.paymentservice.factory;

import com.paypay.paymentservice.enums.PaymentType;
import com.paypay.paymentservice.paymentsource.PaymentSource;
import com.paypay.paymentservice.paymentsource.impl.CreditCard;
import com.paypay.paymentservice.paymentsource.impl.LoyaltyPoints;
import com.paypay.paymentservice.paymentsource.impl.VirtualCard;
import com.paypay.paymentservice.paymentsource.impl.Wallet;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PaymentSourceFactory {

    public static PaymentSource create(PaymentType type, long balance) {
        return switch (type) {
            case LOYALTY_POINTS -> new LoyaltyPoints(balance);
            case WALLET -> new Wallet(balance);
            case VIRTUAL_CARD -> new VirtualCard(balance);
            case CREDIT_CARD -> new CreditCard(balance);
            default -> throw new IllegalArgumentException("Invalid payment type");
        };
    }
}
