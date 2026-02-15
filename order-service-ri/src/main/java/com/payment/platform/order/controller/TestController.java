package com.payment.platform.order.controller;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.GenericEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.payment.platform.order.command.CreateOrderCommand;
import com.payment.platform.core.events.PaymentInitiatedEvent;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {
    
    @Autowired
    private CommandGateway commandGateway;
    
    @Autowired
    private EventBus eventBus;
    
    @PostMapping("/create-order-and-pay")
    public String createOrderAndPay() {
        log.info("Starting complete order and payment test flow");
        
        try {
            // Step 1: Create an order
            String orderId = UUID.randomUUID().toString();
            String userId = "test-user-123";
            String productId = "test-product-456";
            String productName = "Test Product";
            Integer quantity = 2;
            BigDecimal unitPrice = new BigDecimal("99.99");
            BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
            
            CreateOrderCommand createOrderCommand = new CreateOrderCommand(
                orderId, userId, productId, productName, quantity, unitPrice, totalAmount
            );
            
            commandGateway.sendAndWait(createOrderCommand);
            log.info("✅ Order created: {}", orderId);
            
            // Step 2: Initiate payment (simulating what would happen in a real system)
            String paymentId = UUID.randomUUID().toString();
            PaymentInitiatedEvent paymentEvent = new PaymentInitiatedEvent(
                paymentId, orderId, totalAmount.toString(), "USD", userId, 
                "test-merchant-789", "CREDIT_CARD"
            );
            
            eventBus.publish(GenericEventMessage.asEventMessage(paymentEvent));
            log.info("✅ Payment initiated: {}", paymentId);
            
            return String.format("Test flow started! Order: %s, Payment: %s", orderId, paymentId);
            
        } catch (Exception e) {
            log.error("❌ Test flow failed", e);
            return "Test flow failed: " + e.getMessage();
        }
    }
    
    @GetMapping("/status")
    public String status() {
        return "Order Service is running! Ready to handle orders and payment events.";
    }
}