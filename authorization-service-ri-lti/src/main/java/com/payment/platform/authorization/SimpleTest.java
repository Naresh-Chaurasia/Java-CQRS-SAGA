package com.payment.platform.authorization;

import com.payment.platform.core.events.PaymentInitiatedEvent;

public class SimpleTest {
    public void test() {
        PaymentInitiatedEvent event = new PaymentInitiatedEvent();
        // Test if we can access the field directly
        String amount = "100.00";
        event.setAmount(amount);
        System.out.println("Field set: " + amount);
    }
}
