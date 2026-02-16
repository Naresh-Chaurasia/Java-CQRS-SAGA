package com.payment.platform.reconciliation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Represents result of a reconciliation operation between payments and settlements.
 * 
 * This model captures:
 * - Overall reconciliation status and statistics
 * - Detailed mismatch information
 * - Timestamps and correlation IDs for traceability
 */
public class ReconciliationResult {
    
    private String reconciliationId;
    private LocalDateTime reconciliationDate;
    private String status; // COMPLETED, PARTIAL, FAILED
    private ReconciliationStats stats;
    private List<ReconciliationMismatch> mismatches;
    private String correlationId;
    
    // Default constructor
    public ReconciliationResult() {}
    
    // All args constructor
    public ReconciliationResult(String reconciliationId, LocalDateTime reconciliationDate, String status, 
                           ReconciliationStats stats, List<ReconciliationMismatch> mismatches, String correlationId) {
        this.reconciliationId = reconciliationId;
        this.reconciliationDate = reconciliationDate;
        this.status = status;
        this.stats = stats;
        this.mismatches = mismatches;
        this.correlationId = correlationId;
    }
    
    // Getters and Setters
    public String getReconciliationId() { return reconciliationId; }
    public void setReconciliationId(String reconciliationId) { this.reconciliationId = reconciliationId; }
    
    public LocalDateTime getReconciliationDate() { return reconciliationDate; }
    public void setReconciliationDate(LocalDateTime reconciliationDate) { this.reconciliationDate = reconciliationDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public ReconciliationStats getStats() { return stats; }
    public void setStats(ReconciliationStats stats) { this.stats = stats; }
    
    public List<ReconciliationMismatch> getMismatches() { return mismatches; }
    public void setMismatches(List<ReconciliationMismatch> mismatches) { this.mismatches = mismatches; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReconciliationResult that = (ReconciliationResult) o;
        return Objects.equals(reconciliationId, that.reconciliationId) &&
               Objects.equals(status, that.status);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(reconciliationId, status);
    }
    
    // toString
    @Override
    public String toString() {
        return "ReconciliationResult{" +
                "reconciliationId='" + reconciliationId + '\'' +
                ", status='" + status + '\'' +
                ", mismatchesCount=" + (mismatches != null ? mismatches.size() : 0) +
                '}';
    }
    
    /**
     * Statistics for reconciliation operations.
     */
    public static class ReconciliationStats {
        private int totalPayments;
        private int totalSettlements;
        private int matchedTransactions;
        private int unmatchedPayments;
        private int unmatchedSettlements;
        private BigDecimal totalAmount;
        private BigDecimal matchedAmount;
        private BigDecimal mismatchedAmount;
        
        // Default constructor
        public ReconciliationStats() {}
        
        // All args constructor
        public ReconciliationStats(int totalPayments, int totalSettlements, int matchedTransactions,
                               int unmatchedPayments, int unmatchedSettlements, BigDecimal totalAmount,
                               BigDecimal matchedAmount, BigDecimal mismatchedAmount) {
            this.totalPayments = totalPayments;
            this.totalSettlements = totalSettlements;
            this.matchedTransactions = matchedTransactions;
            this.unmatchedPayments = unmatchedPayments;
            this.unmatchedSettlements = unmatchedSettlements;
            this.totalAmount = totalAmount;
            this.matchedAmount = matchedAmount;
            this.mismatchedAmount = mismatchedAmount;
        }
        
        // Getters and Setters
        public int getTotalPayments() { return totalPayments; }
        public void setTotalPayments(int totalPayments) { this.totalPayments = totalPayments; }
        
        public int getTotalSettlements() { return totalSettlements; }
        public void setTotalSettlements(int totalSettlements) { this.totalSettlements = totalSettlements; }
        
        public int getMatchedTransactions() { return matchedTransactions; }
        public void setMatchedTransactions(int matchedTransactions) { this.matchedTransactions = matchedTransactions; }
        
        public int getUnmatchedPayments() { return unmatchedPayments; }
        public void setUnmatchedPayments(int unmatchedPayments) { this.unmatchedPayments = unmatchedPayments; }
        
        public int getUnmatchedSettlements() { return unmatchedSettlements; }
        public void setUnmatchedSettlements(int unmatchedSettlements) { this.unmatchedSettlements = unmatchedSettlements; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public BigDecimal getMatchedAmount() { return matchedAmount; }
        public void setMatchedAmount(BigDecimal matchedAmount) { this.matchedAmount = matchedAmount; }
        
        public BigDecimal getMismatchedAmount() { return mismatchedAmount; }
        public void setMismatchedAmount(BigDecimal mismatchedAmount) { this.mismatchedAmount = mismatchedAmount; }
        
        // toString
        @Override
        public String toString() {
            return "ReconciliationStats{" +
                    "totalPayments=" + totalPayments +
                    ", matchedTransactions=" + matchedTransactions +
                    ", unmatchedPayments=" + unmatchedPayments +
                    '}';
        }
    }
    
    /**
     * Represents a reconciliation mismatch.
     */
    public static class ReconciliationMismatch {
        private String paymentId;
        private String settlementId;
        private String orderId;
        private String mismatchType; // MISSING_SETTLEMENT, DUPLICATE_SETTLEMENT, AMOUNT_MISMATCH
        private String expectedAmount;
        private String actualAmount;
        private String description;
        private LocalDateTime detectedAt;
        private String severity; // HIGH, MEDIUM, LOW
        
        // Default constructor
        public ReconciliationMismatch() {}
        
        // All args constructor
        public ReconciliationMismatch(String paymentId, String settlementId, String orderId,
                                 String mismatchType, String expectedAmount, String actualAmount,
                                 String description, LocalDateTime detectedAt, String severity) {
            this.paymentId = paymentId;
            this.settlementId = settlementId;
            this.orderId = orderId;
            this.mismatchType = mismatchType;
            this.expectedAmount = expectedAmount;
            this.actualAmount = actualAmount;
            this.description = description;
            this.detectedAt = detectedAt;
            this.severity = severity;
        }
        
        // Getters and Setters
        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        
        public String getSettlementId() { return settlementId; }
        public void setSettlementId(String settlementId) { this.settlementId = settlementId; }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getMismatchType() { return mismatchType; }
        public void setMismatchType(String mismatchType) { this.mismatchType = mismatchType; }
        
        public String getExpectedAmount() { return expectedAmount; }
        public void setExpectedAmount(String expectedAmount) { this.expectedAmount = expectedAmount; }
        
        public String getActualAmount() { return actualAmount; }
        public void setActualAmount(String actualAmount) { this.actualAmount = actualAmount; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public LocalDateTime getDetectedAt() { return detectedAt; }
        public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        // toString
        @Override
        public String toString() {
            return "ReconciliationMismatch{" +
                    "paymentId='" + paymentId + '\'' +
                    ", mismatchType='" + mismatchType + '\'' +
                    ", severity='" + severity + '\'' +
                    '}';
        }
    }
}
