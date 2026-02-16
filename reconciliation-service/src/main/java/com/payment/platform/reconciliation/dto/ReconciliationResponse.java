package com.payment.platform.reconciliation.dto;

import com.payment.platform.reconciliation.model.ReconciliationResult;
import java.util.List;
import java.util.Objects;

/**
 * DTO for reconciliation responses.
 * 
 * Used for:
 * - API response contracts
 * - Standardized response format
 * - Success/error status reporting
 */
public class ReconciliationResponse {
    
    private boolean success;
    private String reconciliationId;
    private String status;
    private String message;
    private ReconciliationResult.ReconciliationStats stats;
    private List<ReconciliationResult.ReconciliationMismatch> mismatches;
    private String correlationId;
    private Long timestamp;
    
    // Default constructor
    public ReconciliationResponse() {}
    
    // Success message constructor
    public ReconciliationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    // All args constructor
    public ReconciliationResponse(boolean success, String reconciliationId, String status, String message,
                            ReconciliationResult.ReconciliationStats stats, 
                            List<ReconciliationResult.ReconciliationMismatch> mismatches, 
                            String correlationId, Long timestamp) {
        this.success = success;
        this.reconciliationId = reconciliationId;
        this.status = status;
        this.message = message;
        this.stats = stats;
        this.mismatches = mismatches;
        this.correlationId = correlationId;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getReconciliationId() { return reconciliationId; }
    public void setReconciliationId(String reconciliationId) { this.reconciliationId = reconciliationId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public ReconciliationResult.ReconciliationStats getStats() { return stats; }
    public void setStats(ReconciliationResult.ReconciliationStats stats) { this.stats = stats; }
    
    public List<ReconciliationResult.ReconciliationMismatch> getMismatches() { return mismatches; }
    public void setMismatches(List<ReconciliationResult.ReconciliationMismatch> mismatches) { this.mismatches = mismatches; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReconciliationResponse that = (ReconciliationResponse) o;
        return Objects.equals(reconciliationId, that.reconciliationId) &&
               Objects.equals(success, that.success);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(reconciliationId, success);
    }
    
    // toString
    @Override
    public String toString() {
        return "ReconciliationResponse{" +
                "success=" + success +
                ", reconciliationId='" + reconciliationId + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
