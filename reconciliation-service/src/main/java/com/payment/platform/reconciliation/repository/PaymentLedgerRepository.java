package com.payment.platform.reconciliation.repository;

import com.payment.platform.reconciliation.model.PaymentLedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PaymentLedgerEntry entities.
 * 
 * Provides custom queries for reconciliation operations:
 * - Find entries by reconciliation status
 * - Find entries within date ranges
 * - Count mismatches by type
 */
@Repository
public interface PaymentLedgerRepository extends JpaRepository<PaymentLedgerEntry, String> {
    
    List<PaymentLedgerEntry> findByReconciliationStatus(String reconciliationStatus);
    
    List<PaymentLedgerEntry> findByPaymentStatusAndReconciliationStatus(
        String paymentStatus, String reconciliationStatus);
    
    List<PaymentLedgerEntry> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT p FROM PaymentLedgerEntry p WHERE p.paymentStatus = 'SETTLED' AND p.settlementId IS NULL")
    List<PaymentLedgerEntry> findSettledPaymentsWithoutSettlementId();
    
    @Query("SELECT p FROM PaymentLedgerEntry p WHERE p.settlementId IS NOT NULL AND p.paymentStatus != 'SETTLED'")
    List<PaymentLedgerEntry> findSettlementsWithoutSettledStatus();
    
    @Query("SELECT COUNT(p) FROM PaymentLedgerEntry p WHERE p.reconciliationStatus = :status")
    long countByReconciliationStatus(@Param("status") String status);
    
    @Query("SELECT p FROM PaymentLedgerEntry p WHERE p.orderId = :orderId")
    List<PaymentLedgerEntry> findByOrderId(@Param("orderId") String orderId);
    
    @Query("SELECT p FROM PaymentLedgerEntry p WHERE p.correlationId = :correlationId")
    Optional<PaymentLedgerEntry> findByCorrelationId(@Param("correlationId") String correlationId);
}
