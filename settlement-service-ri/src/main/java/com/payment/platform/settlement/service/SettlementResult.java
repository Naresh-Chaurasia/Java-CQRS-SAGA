package com.payment.platform.settlement.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Result object containing the outcome of payment settlement processing.
 * 
 * This class encapsulates the decision made by SettlementProcessor:
 * - settled: Whether the payment was successfully settled with provider
 * - settlementId: Unique identifier from payment provider
 * - failureReasons: List of reasons for settlement failure
 * - retryCount: Number of retry attempts made
 * 
 * Used by: SettlementEventHandler to determine which event to publish
 * Purpose: Provides structured settlement outcome for downstream processing
 * 
 * Error Codes:
 * - PROVIDER_ERROR: Payment provider returned error
 * - INSUFFICIENT_FUNDS: Account lacks sufficient funds
 * - INVALID_CARD: Card details are invalid
 * - NETWORK_ERROR: Network connectivity issues
 * - MAX_RETRIES_EXCEEDED: Retry limit reached
 */
public class SettlementResult {
    private boolean settled = false;
    private String settlementId;
    private List<String> failureReasons = new ArrayList<>();
    private int retryCount = 0;
    
    public void addFailure(String code, String reason) {
        settled = false;
        failureReasons.add(code + ": " + reason);
    }
    
    public void markAsSettled(String settlementId) {
        this.settled = true;
        this.settlementId = settlementId;
        this.failureReasons.clear();
    }
    
    public void incrementRetryCount() {
        this.retryCount++;
    }
    
    // Manual getters and setters
    public boolean isSettled() { return settled; }
    public void setSettled(boolean settled) { this.settled = settled; }
    
    public String getSettlementId() { return settlementId; }
    public void setSettlementId(String settlementId) { this.settlementId = settlementId; }
    
    public List<String> getFailureReasons() { return failureReasons; }
    public void setFailureReasons(List<String> failureReasons) { this.failureReasons = failureReasons; }
    
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
}
