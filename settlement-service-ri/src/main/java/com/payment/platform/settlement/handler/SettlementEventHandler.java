package com.payment.platform.settlement.handler;

import com.payment.platform.settlement.service.SettlementProcessor;
import com.payment.platform.settlement.service.SettlementResult;
import com.payment.platform.core.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.GenericEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Event handler responsible for processing payment authorization events and publishing
 * settlement results (settled/failed) to the payment platform.
 * 
 * This handler integrates with the CQRS event-driven architecture:
 * - Receives PaymentAuthorizedEvent via Axon Framework
 * - Delegates to SettlementProcessor for payment processing
 * - Publishes PaymentSettledEvent or handles settlement failures
 * - Handles exceptions and implements retry logic
 * 
 * Key Responsibilities:
 * - Payment settlement workflow orchestration
 * - Event publishing for downstream services (OrderService)
 * - Error handling and logging for audit trails
 * - Integration with Axon Framework for distributed event handling
 * 
 * Architecture Position: Critical component in payment processing pipeline
 * Input: PaymentAuthorizedEvent from AuthorizationService
 * Output: PaymentSettledEvent for OrderService (on success)
 */
@Component
public class SettlementEventHandler {
    
    private static final Logger log = LoggerFactory.getLogger(SettlementEventHandler.class);
    
    public SettlementEventHandler() {
        log.info("SettlementEventHandler initialized and ready to receive events");
    }
    
    @Autowired
    private SettlementProcessor settlementProcessor;
    
    @Autowired
    private CommandGateway commandGateway;
    
    @Autowired
    private EventBus eventBus;
    
    @EventHandler
    public void on(PaymentAuthorizedEvent event) {
        System.out.println("1. Processing payment settlement for authorized paymentId:");
        log.info("1. Processing payment settlement for authorized paymentId: {}", event.getPaymentId());
        log.info("Event received - PaymentId: {}, OrderId: {}, AuthCode: {}, RiskScore: {}, Amount: {}", 
                event.getPaymentId(), event.getOrderId(), event.getAuthorizationCode(), event.getRiskScore(), event.getAmount());
        
        try {
            log.info("2. Delegating to SettlementProcessor for payment: {}", event.getPaymentId());
            SettlementResult result = settlementProcessor.processSettlement(event);
            
            if (result.isSettled()) {
                // Create and publish PaymentSettledEvent
                PaymentSettledEvent settledEvent = new PaymentSettledEvent(
                    event.getPaymentId(),
                    event.getOrderId(),
                    result.getSettlementId(),
                    LocalDateTime.now()
                );
                
                eventBus.publish(GenericEventMessage.asEventMessage(settledEvent));
                log.info("4. Payment settlement completed: {}, settlementId: {}", 
                        event.getPaymentId(), result.getSettlementId());
                
            } else {
                // Settlement failed - log and potentially trigger compensation
                log.error("4. Payment settlement failed for paymentId: {}, attempts: {}, reasons: {}", 
                        event.getPaymentId(), result.getRetryCount(), result.getFailureReasons());
                
                // In a real system, you might:
                // 1. Send notification to customer service
                // 2. Update payment status in database
                // 3. Trigger manual review process
                // 4. Send PaymentFailedEvent to OrderService for compensation
            }
            
        } catch (Exception e) {
            log.error("Settlement processing failed for paymentId: {}", event.getPaymentId(), e);
            
            // In a real system, you might:
            // 1. Queue for retry later
            // 2. Send alert to operations team
            // 3. Update payment status to FAILED
        }
    }
}
