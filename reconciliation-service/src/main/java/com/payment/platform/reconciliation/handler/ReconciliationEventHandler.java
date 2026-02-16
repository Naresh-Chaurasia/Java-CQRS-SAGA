package com.payment.platform.reconciliation.handler;

import com.payment.platform.core.events.*;
import com.payment.platform.reconciliation.model.PaymentLedgerEntry;
import com.payment.platform.reconciliation.repository.PaymentLedgerRepository;

import lombok.extern.slf4j.Slf4j;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Event handler responsible for maintaining the payment ledger for reconciliation.
 * 
 * This handler:
 * - Captures all payment-related events from the event stream
 * - Updates the payment ledger with current payment state
 * - Ensures data consistency for reconciliation operations
 * - Provides audit trail for payment lifecycle
 */
@Component
@ProcessingGroup("reconciliation-group")
@Slf4j
public class ReconciliationEventHandler {
    
    @Autowired
    private PaymentLedgerRepository paymentLedgerRepository;
    
    @EventHandler
    public void on(PaymentInitiatedEvent event) {
        log.info("Processing PaymentInitiatedEvent for ledger: paymentId={}, orderId={}", 
                event.getPaymentId(), event.getOrderId());
        
        PaymentLedgerEntry entry = new PaymentLedgerEntry();
        entry.setPaymentId(event.getPaymentId());
        entry.setOrderId(event.getOrderId());
        entry.setPaymentStatus("INITIATED");
        entry.setAmount(new BigDecimal(event.getAmount()));
        entry.setCreatedAt(event.getTimestamp());
        entry.setCorrelationId(UUID.randomUUID().toString());
        
        paymentLedgerRepository.save(entry);
        log.info("Payment ledger entry created for initiated payment: {}", event.getPaymentId());
    }
    
    @EventHandler
    public void on(PaymentAuthorizedEvent event) {
        log.info("Processing PaymentAuthorizedEvent for ledger: paymentId={}, authCode={}", 
                event.getPaymentId(), event.getAuthorizationCode());
        
        Optional<PaymentLedgerEntry> existingEntry = paymentLedgerRepository.findById(event.getPaymentId());
        
        if (existingEntry.isPresent()) {
            PaymentLedgerEntry entry = existingEntry.get();
            entry.setPaymentStatus("AUTHORIZED");
            entry.setAuthorizationCode(event.getAuthorizationCode());
            entry.setRiskScore(event.getRiskScore());
            entry.setAmount(new BigDecimal(event.getAmount()));
            
            paymentLedgerRepository.save(entry);
            log.info("Payment ledger updated for authorized payment: {}", event.getPaymentId());
        } else {
            log.warn("Payment ledger entry not found for authorized payment: {}", event.getPaymentId());
        }
    }
    
    @EventHandler
    public void on(PaymentRejectedEvent event) {
        log.info("Processing PaymentRejectedEvent for ledger: paymentId={}, reason={}", 
                event.getPaymentId(), event.getRejectionReason());
        
        Optional<PaymentLedgerEntry> existingEntry = paymentLedgerRepository.findById(event.getPaymentId());
        
        if (existingEntry.isPresent()) {
            PaymentLedgerEntry entry = existingEntry.get();
            entry.setPaymentStatus("REJECTED");
            entry.setRejectionReason(event.getRejectionReason());
            
            paymentLedgerRepository.save(entry);
            log.info("Payment ledger updated for rejected payment: {}", event.getPaymentId());
        } else {
            log.warn("Payment ledger entry not found for rejected payment: {}", event.getPaymentId());
        }
    }
    
    @EventHandler
    public void on(PaymentSettledEvent event) {
        log.info("Processing PaymentSettledEvent for ledger: paymentId={}, settlementId={}", 
                event.getPaymentId(), event.getSettlementId());
        
        Optional<PaymentLedgerEntry> existingEntry = paymentLedgerRepository.findById(event.getPaymentId());
        
        if (existingEntry.isPresent()) {
            PaymentLedgerEntry entry = existingEntry.get();
            entry.setPaymentStatus("SETTLED");
            entry.setSettlementId(event.getSettlementId());
            entry.setSettlementDate(event.getSettlementDate());
            
            paymentLedgerRepository.save(entry);
            log.info("Payment ledger updated for settled payment: {}", event.getPaymentId());
        } else {
            log.warn("Payment ledger entry not found for settled payment: {}", event.getPaymentId());
        }
    }
}
