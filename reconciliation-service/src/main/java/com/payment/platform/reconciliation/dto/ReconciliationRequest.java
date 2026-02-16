package com.payment.platform.reconciliation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for reconciliation requests.
 * 
 * Used for:
 * - Date range reconciliation requests
 * - Validation of input parameters
 * - API request/response contracts
 */
public class ReconciliationRequest {
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in future")
    private LocalDateTime endDate;
    
    private String correlationId;
    private String reason; // MANUAL, SCHEDULED, ERROR_RECOVERY
    
    // Default constructor
    public ReconciliationRequest() {}
    
    // All args constructor
    public ReconciliationRequest(LocalDateTime startDate, LocalDateTime endDate, String correlationId, String reason) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.correlationId = correlationId;
        this.reason = reason;
    }
    
    // Getters and Setters
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReconciliationRequest that = (ReconciliationRequest) o;
        return Objects.equals(startDate, that.startDate) &&
               Objects.equals(endDate, that.endDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }
    
    // toString
    @Override
    public String toString() {
        return "ReconciliationRequest{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", reason='" + reason + '\'' +
                '}';
    }
}
