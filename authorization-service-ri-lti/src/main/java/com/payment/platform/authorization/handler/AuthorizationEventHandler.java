/**
 * Event handler responsible for processing payment initiation events and publishing
 * authorization results (approved/rejected) to the payment platform.
 * 
 * This handler integrates with the CQRS event-driven architecture:
 * - Receives PaymentInitiatedEvent via Axon Framework
 * - Delegates to AuthorizationRulesEngine for business rule evaluation
 * - Publishes PaymentAuthorizedEvent or PaymentRejectedEvent based on evaluation
 * - Handles exceptions and publishes appropriate error events
 * 
 * Key Responsibilities:
 * - Payment authorization workflow orchestration
 * - Event publishing for downstream services (SettlementService, OrderService)
 * - Error handling and logging for audit trails
 * - Integration with Axon Framework for distributed command/event handling
 * 
 * Architecture Position: Critical component in payment processing pipeline
 * Downstream Dependencies: SettlementService for approved payments, OrderService for failures
 */
package com.payment.platform.authorization.handler;

import com.payment.platform.authorization.service.AuthorizationResult;
import com.payment.platform.authorization.service.AuthorizationRulesEngine;
import com.payment.platform.core.events.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.GenericEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class AuthorizationEventHandler {
    
    @Autowired
    private AuthorizationRulesEngine rulesEngine;
    
    @Autowired
    private CommandGateway commandGateway;
    
    @Autowired
    private EventBus eventBus;
    
    @EventHandler
    public void on(PaymentInitiatedEvent event) {
        log.info("1. Processing payment authorization for paymentId: {}", event.getPaymentId());
        
        try {
            AuthorizationResult result = rulesEngine.evaluate(event);
            
            if (result.isApproved()) {
                PaymentAuthorizedEvent authorizedEvent = new PaymentAuthorizedEvent(
                    event.getPaymentId(),
                    event.getOrderId(),
                    generateAuthCode(),
                    result.getRiskScore(),
                    event.getAmount()
                );
                
                eventBus.publish(GenericEventMessage.asEventMessage(authorizedEvent));
                log.info("4. Payment authorized: {}", event.getPaymentId());
                
            } else {
                PaymentRejectedEvent rejectedEvent = new PaymentRejectedEvent(
                    event.getPaymentId(),
                    event.getOrderId(),
                    String.join("; ", result.getRejectionReasons()),
                    "AUTH_FAILED"
                );
                
                commandGateway.send(rejectedEvent);
                log.warn("Payment rejected: {}, reasons: {}", event.getPaymentId(), result.getRejectionReasons());
            }
            
        } catch (Exception e) {
            log.error("Authorization failed for paymentId: {}", event.getPaymentId(), e);
            
            PaymentRejectedEvent rejectedEvent = new PaymentRejectedEvent(
                    event.getPaymentId(),
                    event.getOrderId(),
                    "Authorization failed: " + e.getMessage(),
                    "SYSTEM_ERROR"
                );
            
            commandGateway.send(rejectedEvent);
        }
    }
    
    private String generateAuthCode() {
        return "AUTH_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
