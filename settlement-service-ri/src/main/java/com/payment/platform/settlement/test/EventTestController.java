package com.payment.platform.settlement.test;

import com.payment.platform.core.events.PaymentAuthorizedEvent;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.GenericEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Test controller to verify event handling in settlement service.
 * 
 * This controller helps debug event reception by publishing test events
 * and verifying they are processed by the event handler.
 */
@RestController
@RequestMapping("/api/test")
public class EventTestController {
    
    private static final Logger log = LoggerFactory.getLogger(EventTestController.class);
    
    @Autowired
    private EventBus eventBus;
    
    @PostMapping("/test-event")
    public String testEvent(@RequestBody TestEventRequest request) {
        log.info("Publishing test PaymentAuthorizedEvent for paymentId: {}", request.getPaymentId());
        
        // Create a test event
        PaymentAuthorizedEvent event = new PaymentAuthorizedEvent(
            request.getPaymentId(),
            request.getOrderId(),
            "AUTH_TEST_" + System.currentTimeMillis(),
            "25",
            "100.00"
        );
        
        // Publish event to Axon (this should trigger SettlementEventHandler)
        eventBus.publish(GenericEventMessage.asEventMessage(event));
        
        return "Test event published for paymentId: " + request.getPaymentId();
    }
    
    public static class TestEventRequest {
        private String paymentId;
        private String orderId;
        
        // Getters and setters
        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
    }
}
