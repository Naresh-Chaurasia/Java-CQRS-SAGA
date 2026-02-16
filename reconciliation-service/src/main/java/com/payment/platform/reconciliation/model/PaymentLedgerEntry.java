package com.payment.platform.reconciliation.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a payment ledger entry for reconciliation.
 * 
 * This entity stores:
 * - Payment information from PaymentService events
 * - Settlement information from SettlementService events
 * - Reconciliation status and metadata
 */
@Entity
@Table(name = "payment_ledger")
public class PaymentLedgerEntry {
    
    @Id
    private String paymentId;
    
    @Column(nullable = false)
    private String orderId;
    
    @Column(name = "payment_status")
    private String paymentStatus; // INITIATED, AUTHORIZED, REJECTED, SETTLED
    
    @Column(name = "settlement_id")
    private String settlementId;
    
    @Column(name = "settlement_date")
    private LocalDateTime settlementDate;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "authorization_code")
    private String authorizationCode;
    
    @Column(name = "risk_score")
    private String riskScore;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "reconciliation_status")
    private String reconciliationStatus; // PENDING, MATCHED, MISMATCH, EXCLUDED
    
    @Column(name = "last_reconciled_at")
    private LocalDateTime lastReconciledAt;
    
    @Column(name = "correlation_id")
    private String correlationId;
    
    // Default constructor
    public PaymentLedgerEntry() {}
    
    // All args constructor
    public PaymentLedgerEntry(String paymentId, String orderId, String paymentStatus, 
                           String settlementId, LocalDateTime settlementDate, BigDecimal amount,
                           String authorizationCode, String riskScore, String rejectionReason,
                           LocalDateTime createdAt, LocalDateTime updatedAt, String reconciliationStatus,
                           LocalDateTime lastReconciledAt, String correlationId) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
        this.settlementId = settlementId;
        this.settlementDate = settlementDate;
        this.amount = amount;
        this.authorizationCode = authorizationCode;
        this.riskScore = riskScore;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reconciliationStatus = reconciliationStatus;
        this.lastReconciledAt = lastReconciledAt;
        this.correlationId = correlationId;
    }
    
    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getSettlementId() { return settlementId; }
    public void setSettlementId(String settlementId) { this.settlementId = settlementId; }
    
    public LocalDateTime getSettlementDate() { return settlementDate; }
    public void setSettlementDate(LocalDateTime settlementDate) { this.settlementDate = settlementDate; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getAuthorizationCode() { return authorizationCode; }
    public void setAuthorizationCode(String authorizationCode) { this.authorizationCode = authorizationCode; }
    
    public String getRiskScore() { return riskScore; }
    public void setRiskScore(String riskScore) { this.riskScore = riskScore; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getReconciliationStatus() { return reconciliationStatus; }
    public void setReconciliationStatus(String reconciliationStatus) { this.reconciliationStatus = reconciliationStatus; }
    
    public LocalDateTime getLastReconciledAt() { return lastReconciledAt; }
    public void setLastReconciledAt(LocalDateTime lastReconciledAt) { this.lastReconciledAt = lastReconciledAt; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (reconciliationStatus == null) reconciliationStatus = "PENDING";
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentLedgerEntry that = (PaymentLedgerEntry) o;
        return Objects.equals(paymentId, that.paymentId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }
    
    // toString
    @Override
    public String toString() {
        return "PaymentLedgerEntry{" +
                "paymentId='" + paymentId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", reconciliationStatus='" + reconciliationStatus + '\'' +
                '}';
    }
}
