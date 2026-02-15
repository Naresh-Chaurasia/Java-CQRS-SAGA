package com.payment.platform.settlement.service;

import com.payment.platform.core.events.PaymentAuthorizedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for processing payment settlements with external payment providers.
 * 
 * This processor handles the actual payment settlement workflow:
 * - Payment provider integration (mock for development)
 * - Retry logic for failed settlements
 * - Settlement status tracking and logging
 * - Error handling and classification
 * 
 * Key Features:
 * - Configurable retry attempts and delays
 * - Mock payment provider for testing
 * - Detailed error classification
 * - Settlement audit logging
 * 
 * Used by: SettlementEventHandler to process PaymentAuthorizedEvent
 * Output: SettlementResult with detailed settlement status
 * 
 * Architecture Role: Business logic layer in settlement microservice
 */
@Service
public class SettlementProcessor {
    
    private static final Logger log = LoggerFactory.getLogger(SettlementProcessor.class);
    
    @Value("${settlement.retry.max-attempts:3}")
    private int maxRetryAttempts;
    
    @Value("${settlement.retry.delay-ms:1000}")
    private long retryDelayMs;
    
    @Value("${settlement.payment.provider.mock:true}")
    private boolean useMockProvider;
    
    private final Random random = new Random();
    
    public SettlementResult processSettlement(PaymentAuthorizedEvent event) {
        log.info("2. Processing settlement for authorized payment: {}", event.getPaymentId());
        log.info("Payment details - Amount: {}, Auth Code: {}, Risk Score: {}", 
                event.getAmount(), event.getAuthorizationCode(), event.getRiskScore());
        
        SettlementResult result = new SettlementResult();
        
        for (int attempt = 1; attempt <= maxRetryAttempts; attempt++) {
            result.incrementRetryCount();
            
            try {
                log.info("Attempt {} to settle payment: {}", attempt, event.getPaymentId());
                
                // Simulate payment provider processing
                boolean settlementSuccess = processWithProvider(event);
                
                if (settlementSuccess) {
                    String settlementId = generateSettlementId();
                    result.markAsSettled(settlementId);
                    log.info("3. Payment settled successfully: {}, settlementId: {}", 
                            event.getPaymentId(), settlementId);
                    return result;
                } else {
                    String failureReason = determineFailureReason(event);
                    result.addFailure("PROVIDER_ERROR", failureReason);
                    log.warn("Settlement attempt {} failed for payment: {}, reason: {}", 
                            attempt, event.getPaymentId(), failureReason);
                    
                    if (attempt < maxRetryAttempts) {
                        log.info("Waiting {}ms before retry...", retryDelayMs);
                        TimeUnit.MILLISECONDS.sleep(retryDelayMs);
                    }
                }
                
            } catch (Exception e) {
                result.addFailure("SYSTEM_ERROR", "Settlement processing failed: " + e.getMessage());
                log.error("Settlement processing error for payment: {}", event.getPaymentId(), e);
                
                if (attempt < maxRetryAttempts) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        log.error("4. Settlement failed after {} attempts for payment: {}", 
                maxRetryAttempts, event.getPaymentId());
        result.addFailure("MAX_RETRIES_EXCEEDED", "Maximum retry attempts exceeded");
        
        return result;
    }
    
    private boolean processWithProvider(PaymentAuthorizedEvent event) {
        if (useMockProvider) {
            // Mock payment provider - 100% success rate for testing
            return random.nextDouble() < 1.0;
        }
        
        // Real payment provider integration would go here
        // For now, simulate success
        return true;
    }
    
    private String determineFailureReason(PaymentAuthorizedEvent event) {
        // Simulate different failure reasons based on random selection
        double rand = random.nextDouble();
        
        if (rand < 0.3) {
            return "Insufficient funds in account";
        } else if (rand < 0.6) {
            return "Invalid card details";
        } else if (rand < 0.8) {
            return "Payment provider network error";
        } else {
            return "Transaction declined by risk assessment";
        }
    }
    
    private String generateSettlementId() {
        return "SETTLE_" + System.currentTimeMillis() + "_" + 
               String.format("%04d", random.nextInt(10000));
    }
}
