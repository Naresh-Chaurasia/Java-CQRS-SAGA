package com.payment.platform.authorization.handler;

import com.payment.platform.authorization.service.AuthorizationRulesEngine;
import com.payment.platform.core.events.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
public class AuthorizationEventHandler {
    
    @Autowired
    private AuthorizationRulesEngine rulesEngine;
    
    @Autowired
    private CommandGateway commandGateway;
    
    @EventHandler
    public void on(PaymentInitiatedEvent event) {
        log.info("Processing payment authorization for paymentId: {}", event.getPaymentId());
        
        try {
            AuthorizationResult result = rulesEngine.evaluate(event);
            
            if (result.isApproved()) {
                PaymentAuthorizedEvent authorizedEvent = PaymentAuthorizedEvent.builder()
                        .paymentId(event.getPaymentId())
                        .orderId(event.getOrderId())
                        .authorizationCode(generateAuthCode())
                        .riskScore(result.getRiskScore())
                        .correlationId(event.getCorrelationId())
                        .timestamp(LocalDateTime.now())
                        .build();
                
                commandGateway.send(authorizedEvent);
                log.info("Payment authorized: {}", event.getPaymentId());
                
            } else {
                PaymentRejectedEvent rejectedEvent = PaymentRejectedEvent.builder()
                        .paymentId(event.getPaymentId())
                        .orderId(event.getOrderId())
                        .rejectionReason(String.join("; ", result.getRejectionReasons()))
                        .errorCode("AUTH_FAILED")
                        .correlationId(event.getCorrelationId())
                        .timestamp(LocalDateTime.now())
                        .build();
                
                commandGateway.send(rejectedEvent);
                log.warn("Payment rejected: {}, reasons: {}", event.getPaymentId(), result.getRejectionReasons());
            }
            
        } catch (Exception e) {
            log.error("Authorization failed for paymentId: {}", event.getPaymentId(), e);
            
            PaymentRejectedEvent rejectedEvent = PaymentRejectedEvent.builder()
                    .paymentId(event.getPaymentId())
                    .orderId(event.getOrderId())
                    .rejectionReason("System error during authorization")
                    .errorCode("SYSTEM_ERROR")
                    .correlationId(event.getCorrelationId())
                    .timestamp(LocalDateTime.now())
                    .build();
            
            commandGateway.send(rejectedEvent);
        }
    }
    
    private String generateAuthCode() {
        return "AUTH_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
