/**
 * Result object containing the outcome of payment authorization evaluation.
 * 
 * This class encapsulates the decision made by AuthorizationRulesEngine:
 * - approved: Whether the payment passed all validation rules
 * - riskScore: Numerical risk assessment (0-100 scale)
 * - rejectionReasons: List of specific reasons for rejection
 * 
 * Used by: AuthorizationEventHandler to determine which event to publish
 * Purpose: Provides structured authorization outcome for downstream processing
 * 
 * Error Codes:
 * - AMOUNT_EXCEEDS_LIMIT: Transaction amount exceeds daily limit
 * - HIGH_RISK: Transaction flagged as high risk
 * - INVALID_MERCHANT: Merchant not authorized
 * - INVALID_CURRENCY: Currency not supported
 */
package com.payment.platform.authorization.service;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationResult {
    private boolean approved = true;
    private String riskScore;
    private List<String> rejectionReasons = new ArrayList<>();
    
    public void addRejection(String code, String reason) {
        approved = false;
        rejectionReasons.add(code + ": " + reason);
    }
    
    // Manual getters and setters
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    
    public String getRiskScore() { return riskScore; }
    public void setRiskScore(String riskScore) { this.riskScore = riskScore; }
    
    public List<String> getRejectionReasons() { return rejectionReasons; }
    public void setRejectionReasons(List<String> rejectionReasons) { this.rejectionReasons = rejectionReasons; }
}
