package com.payment.platform.reconciliation.service;

import com.payment.platform.reconciliation.model.PaymentLedgerEntry;
import com.payment.platform.reconciliation.model.ReconciliationResult;
import com.payment.platform.reconciliation.repository.PaymentLedgerRepository;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Core reconciliation service that compares payments against settlements.
 * 
 * This service:
 * - Performs batch reconciliation on scheduled intervals
 * - Identifies mismatches between payments and settlements
 * - Generates detailed reconciliation reports
 * - Updates ledger entries with reconciliation status
 */
@Service
@Slf4j
public class ReconciliationService {
    
    private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);
    
    @Autowired
    private PaymentLedgerRepository paymentLedgerRepository;
    
    @Value("${reconciliation.batch.size:100}")
    private int batchSize;
    
    /**
     * Performs full reconciliation of all pending transactions.
     */
    public ReconciliationResult performFullReconciliation() {
        log.info("Starting full reconciliation process");
        
        String reconciliationId = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now();
        
        try {
            List<PaymentLedgerEntry> pendingEntries = paymentLedgerRepository
                    .findByReconciliationStatus("PENDING");
            
            log.info("Found {} pending entries for reconciliation", pendingEntries.size());
            
            ReconciliationResult result = reconcileEntries(pendingEntries, reconciliationId, startTime);
            
            log.info("Reconciliation completed. Status: {}, Matched: {}, Mismatches: {}", 
                    result.getStatus(), 
                    result.getStats().getMatchedTransactions(),
                    result.getMismatches().size());
            
            return result;
            
        } catch (Exception e) {
            log.error("Reconciliation failed for reconciliationId: {}", reconciliationId, e);
            
            ReconciliationResult result = new ReconciliationResult(
                    reconciliationId,
                    startTime,
                    "FAILED",
                    new ReconciliationResult.ReconciliationStats(),
                    new ArrayList<>(),
                    UUID.randomUUID().toString()
            );
            return result;
        }
    }
    
    /**
     * Performs reconciliation for a specific date range.
     */
    public ReconciliationResult performDateRangeReconciliation(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Starting date range reconciliation from {} to {}", startDate, endDate);
        
        String reconciliationId = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now();
        
        List<PaymentLedgerEntry> entries = paymentLedgerRepository
                .findByCreatedAtBetween(startDate, endDate);
        
        return reconcileEntries(entries, reconciliationId, startTime);
    }
    
    /**
     * Performs reconciliation for specific order.
     */
    public ReconciliationResult performOrderReconciliation(String orderId) {
        log.info("Starting order reconciliation for orderId: {}", orderId);
        
        String reconciliationId = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now();
        
        List<PaymentLedgerEntry> entries = paymentLedgerRepository.findByOrderId(orderId);
        
        return reconcileEntries(entries, reconciliationId, startTime);
    }
    
    /**
     * Core reconciliation logic.
     */
    private ReconciliationResult reconcileEntries(List<PaymentLedgerEntry> entries, 
                                               String reconciliationId, 
                                               LocalDateTime startTime) {
        
        List<ReconciliationResult.ReconciliationMismatch> mismatches = new ArrayList<>();
        int matchedTransactions = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal matchedAmount = BigDecimal.ZERO;
        
        for (PaymentLedgerEntry entry : entries) {
            totalAmount = totalAmount.add(entry.getAmount() != null ? entry.getAmount() : BigDecimal.ZERO);
            
            ReconciliationResult.ReconciliationMismatch mismatch = analyzeEntry(entry);
            
            if (mismatch != null) {
                mismatches.add(mismatch);
                entry.setReconciliationStatus("MISMATCH");
                entry.setLastReconciledAt(LocalDateTime.now());
            } else {
                matchedTransactions++;
                matchedAmount = matchedAmount.add(entry.getAmount() != null ? entry.getAmount() : BigDecimal.ZERO);
                entry.setReconciliationStatus("MATCHED");
                entry.setLastReconciledAt(LocalDateTime.now());
            }
            
            paymentLedgerRepository.save(entry);
        }
        
        // Build statistics
        ReconciliationResult.ReconciliationStats stats = new ReconciliationResult.ReconciliationStats();
        stats.setTotalPayments(entries.size());
        stats.setMatchedTransactions(matchedTransactions);
        stats.setUnmatchedPayments(mismatches.size());
        stats.setTotalAmount(totalAmount);
        stats.setMatchedAmount(matchedAmount);
        stats.setMismatchedAmount(totalAmount.subtract(matchedAmount));
        
        // Determine status
        String status = mismatches.isEmpty() ? "COMPLETED" : 
                       (matchedTransactions > 0 ? "PARTIAL" : "FAILED");
        
        return new ReconciliationResult(
                reconciliationId,
                startTime,
                status,
                stats,
                mismatches,
                UUID.randomUUID().toString()
        );
    }
    
    /**
     * Analyzes a single ledger entry for reconciliation issues.
     */
    private ReconciliationResult.ReconciliationMismatch analyzeEntry(PaymentLedgerEntry entry) {
        
        // Check 1: SETTLED status without settlement ID
        if ("SETTLED".equals(entry.getPaymentStatus()) && entry.getSettlementId() == null) {
            return new ReconciliationResult.ReconciliationMismatch(
                    entry.getPaymentId(),
                    null,
                    entry.getOrderId(),
                    "MISSING_SETTLEMENT_ID",
                    null,
                    null,
                    "Payment marked as SETTLED but missing settlement ID",
                    LocalDateTime.now(),
                    "HIGH"
            );


            // this.paymentId = paymentId;
            // this.settlementId = settlementId;
            // this.orderId = orderId;
            // this.mismatchType = mismatchType;
            // this.expectedAmount = expectedAmount;
            // this.actualAmount = actualAmount;
            // this.description = description;
            // this.detectedAt = detectedAt;
            // this.severity = severity;
       
        }
        
        // Check 2: Settlement ID present but not SETTLED status
        if (entry.getSettlementId() != null && !"SETTLED".equals(entry.getPaymentStatus())) {
            return new ReconciliationResult.ReconciliationMismatch(
                    entry.getPaymentId(),
                    entry.getSettlementId(),
                    entry.getOrderId(),
                    "STATUS_MISMATCH",
                    null,
                    null,
                    "Settlement ID present but payment not marked as SETTLED",
                    LocalDateTime.now(),
                    "MEDIUM"
            );
        }
        
        // Check 3: AUTHORIZED status for too long (potential stuck payment)
        if ("AUTHORIZED".equals(entry.getPaymentStatus()) && 
            entry.getCreatedAt().isBefore(LocalDateTime.now().minusHours(24))) {
            return new ReconciliationResult.ReconciliationMismatch(
                    entry.getPaymentId(),
                    null,
                    entry.getOrderId(),
                    "STUCK_AUTHORIZATION",
                    null,
                    null,
                    "Payment stuck in AUTHORIZED status for over 24 hours",
                    LocalDateTime.now(),
                    "MEDIUM"
            );
        }
        
        // No mismatches found
        return null;
    }
    
    /**
     * Gets reconciliation summary statistics.
     */
    public ReconciliationResult.ReconciliationStats getReconciliationStats() {
        long total = paymentLedgerRepository.count();
        long matched = paymentLedgerRepository.countByReconciliationStatus("MATCHED");
        long mismatched = paymentLedgerRepository.countByReconciliationStatus("MISMATCH");
        long pending = paymentLedgerRepository.countByReconciliationStatus("PENDING");
        
        ReconciliationResult.ReconciliationStats stats = new ReconciliationResult.ReconciliationStats();
        stats.setTotalPayments((int) total);
        stats.setMatchedTransactions((int) matched);
        stats.setUnmatchedPayments((int) mismatched);
        stats.setUnmatchedSettlements((int) pending);
        
        return stats;
    }
}
