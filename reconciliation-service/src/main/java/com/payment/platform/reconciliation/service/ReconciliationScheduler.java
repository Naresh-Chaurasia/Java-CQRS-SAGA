package com.payment.platform.reconciliation.service;

import com.payment.platform.reconciliation.model.ReconciliationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Scheduled reconciliation service that runs automated reconciliation jobs.
 * 
 * This scheduler:
 * - Runs daily reconciliation at configured time
 * - Provides configurable scheduling via properties
 * - Handles failures gracefully with retry logic
 * - Emits structured logs for observability
 */
@Service
@ConditionalOnProperty(name = "reconciliation.schedule.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class ReconciliationScheduler {
    
    @Autowired
    private ReconciliationService reconciliationService;
    
    @Value("${reconciliation.schedule.cron:0 0 2 * * ?}")
    private String cronExpression;
    
    @Value("${reconciliation.retry.max-attempts:3}")
    private int maxRetryAttempts;
    
    @Value("${reconciliation.retry.delay-ms:5000}")
    private long retryDelayMs;
    
    /**
     * Daily reconciliation job scheduled to run at 2 AM by default.
     */
    @Scheduled(cron = "${reconciliation.schedule.cron:0 0 2 * * ?}")
    public void performDailyReconciliation() {
        log.info("Starting scheduled daily reconciliation job at {}", LocalDateTime.now());
        
        int attempt = 0;
        boolean success = false;
        
        while (attempt < maxRetryAttempts && !success) {
            attempt++;
            try {
                log.info("Reconciliation attempt {} of {}", attempt, maxRetryAttempts);
                
                ReconciliationResult result = reconciliationService.performFullReconciliation();
                
                logReconciliationResult(result);
                
                if ("COMPLETED".equals(result.getStatus()) || "PARTIAL".equals(result.getStatus())) {
                    success = true;
                    log.info("Scheduled reconciliation completed successfully on attempt {}", attempt);
                } else {
                    log.warn("Reconciliation failed with status: {}", result.getStatus());
                }
                
            } catch (Exception e) {
                log.error("Reconciliation attempt {} failed", attempt, e);
                
                if (attempt < maxRetryAttempts) {
                    try {
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.warn("Reconciliation retry interrupted");
                        break;
                    }
                }
            }
        }
        
        if (!success) {
            log.error("Scheduled reconciliation failed after {} attempts", maxRetryAttempts);
        }
    }
    
    /**
     * Hourly health check for stuck payments.
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void performHealthCheck() {
        log.debug("Performing reconciliation health check");
        
        try {
            ReconciliationResult.ReconciliationStats stats = reconciliationService.getReconciliationStats();
            
            log.info("Reconciliation health check - Total: {}, Matched: {}, Unmatched: {}", 
                    stats.getTotalPayments(), 
                    stats.getMatchedTransactions(), 
                    stats.getUnmatchedPayments());
            
            // Alert if too many unmatched payments
            if (stats.getUnmatchedPayments() > stats.getTotalPayments() * 0.1) {
                log.warn("High mismatch rate detected: {}% unmatched payments", 
                        (stats.getUnmatchedPayments() * 100.0 / stats.getTotalPayments()));
            }
            
        } catch (Exception e) {
            log.error("Reconciliation health check failed", e);
        }
    }
    
    /**
     * Logs reconciliation result in structured format.
     */
    private void logReconciliationResult(ReconciliationResult result) {
        log.info("Reconciliation completed - ID: {}, Status: {}, Duration: {}ms", 
                result.getReconciliationId(),
                result.getStatus(),
                java.time.Duration.between(result.getReconciliationDate(), java.time.LocalDateTime.now()).toMillis());
        
        log.info("Reconciliation stats - Total: {}, Matched: {}, Mismatches: {}, Amount: ${}", 
                result.getStats().getTotalPayments(),
                result.getStats().getMatchedTransactions(),
                result.getMismatches().size(),
                result.getStats().getTotalAmount());
        
        if (!result.getMismatches().isEmpty()) {
            log.warn("Found {} reconciliation mismatches", result.getMismatches().size());
            
            result.getMismatches().forEach(mismatch -> 
                log.warn("Mismatch - Payment: {}, Type: {}, Severity: {}", 
                        mismatch.getPaymentId(), 
                        mismatch.getMismatchType(), 
                        mismatch.getSeverity()));
        }
    }
}
