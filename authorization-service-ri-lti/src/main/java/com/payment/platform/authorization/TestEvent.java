package com.payment.platform.authorization;

import com.payment.platform.core.events.PaymentInitiatedEvent;

public class TestEvent {
    public static void main(String[] args) {
        PaymentInitiatedEvent event = new PaymentInitiatedEvent();
        System.out.println("Event created successfully");
    }
}
