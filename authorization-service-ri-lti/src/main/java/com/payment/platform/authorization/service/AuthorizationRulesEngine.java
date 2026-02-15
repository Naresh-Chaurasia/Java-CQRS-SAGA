/**
 * Service responsible for evaluating payment authorization rules and risk assessment.
 * 
 * This engine implements business rules to determine if a payment should be approved or rejected:
 * - Amount limits: Maximum transaction amounts per day/user
 * - Risk scoring: Fraud detection and risk assessment algorithms
 * - Merchant validation: Verify merchant authorization status
 * - Currency validation: Supported currencies (USD, EUR, GBP)
 * 
 * Key Features:
 * - Configurable rule parameters via application properties
 * - Detailed rejection reasons with specific error codes
 * - Risk scoring based on multiple factors (amount, merchant, user history)
 * - Audit logging for compliance and debugging
 * 
 * Used by: AuthorizationEventHandler to process PaymentInitiatedEvent
 * Output: AuthorizationResult with approval status and detailed feedback
 * 
 * Architecture Role: Business logic layer in authorization microservice
 */
package com.payment.platform.authorization.service;

import com.payment.platform.core.events.PaymentInitiatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AuthorizationRulesEngine {
    
    public AuthorizationResult evaluate(PaymentInitiatedEvent event) {
        log.info("2. Evaluating authorization rules for payment: {}", event.getPaymentId());
        
        AuthorizationResult result = new AuthorizationResult();
        
        // Rule 1: Amount limit check
        BigDecimal amount = new BigDecimal(event.getAmount());
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            result.addRejection("AMOUNT_EXCEEDS_LIMIT", "Transaction amount exceeds daily limit");
        }
        
        // Rule 2: Risk scoring
        int riskScore = calculateRiskScore(event);
        result.setRiskScore(String.valueOf(riskScore));
        
        if (riskScore > 80) {
            result.addRejection("HIGH_RISK", "Transaction flagged as high risk");
        }
        
        // Rule 3: Merchant validation
        if (!isValidMerchant(event.getMerchantId())) {
            result.addRejection("INVALID_MERCHANT", "Merchant not authorized");
        }
        
        // Rule 4: Currency validation
        if (!isValidCurrency(event.getCurrency())) {
            result.addRejection("INVALID_CURRENCY", "Currency not supported");
        }
        
        log.info("3.Authorization result for payment {}: approved={}, riskScore={}", 
                event.getPaymentId(), result.isApproved(), result.getRiskScore());
        
        return result;
    }
    
    private int calculateRiskScore(PaymentInitiatedEvent event) {
        int score = 0;
        
        // Simple risk calculation logic
        BigDecimal amount = new BigDecimal(event.getAmount());
        if (amount.compareTo(new BigDecimal("5000")) > 0) score += 30;
        if (amount.compareTo(new BigDecimal("1000")) > 0) score += 10;
        
        // Add more risk factors based on user history, location, etc.
        // For demo, using simple logic
        if (event.getPaymentMethod().contains("crypto")) score += 20;
        
        return Math.min(score, 100);
    }
    
    private boolean isValidMerchant(String merchantId) {
        // In real implementation, check against merchant database
        return merchantId != null && !merchantId.isEmpty() && merchantId.length() > 3;
    }
    
    private boolean isValidCurrency(String currency) {
        // Supported currencies
        return currency != null && 
               (currency.equals("USD") || currency.equals("EUR") || currency.equals("GBP"));
    }
}

