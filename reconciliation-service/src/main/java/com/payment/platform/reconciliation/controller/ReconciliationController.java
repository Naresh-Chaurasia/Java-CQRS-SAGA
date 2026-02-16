package com.payment.platform.reconciliation.controller;

import com.payment.platform.reconciliation.dto.ReconciliationRequest;
import com.payment.platform.reconciliation.dto.ReconciliationResponse;
import com.payment.platform.reconciliation.model.ReconciliationResult;
import com.payment.platform.reconciliation.service.ReconciliationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * REST API controller for reconciliation operations.
 * 
 * Provides endpoints for:
 * - Manual reconciliation triggers
 * - Reconciliation reports and statistics
 * - Order-specific reconciliation
 * - Date range reconciliation
 */
@RestController
@RequestMapping("/api/reconciliation")
@Slf4j
public class ReconciliationController {
    
    @Autowired
    private ReconciliationService reconciliationService;
    
    /**
     * Trigger full reconciliation of all pending transactions.
     */
    @PostMapping("/full")
    public ResponseEntity<ReconciliationResponse> triggerFullReconciliation() {
        log.info("Manual full reconciliation triggered via REST API");
        
        try {
            ReconciliationResult result = reconciliationService.performFullReconciliation();
            
            ReconciliationResponse response = new ReconciliationResponse(
                    true,
                    result.getReconciliationId(),
                    result.getStatus(),
                    "Full reconciliation completed successfully",
                    result.getStats(),
                    result.getMismatches(),
                    result.getCorrelationId(),
                    System.currentTimeMillis()
            );

            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Manual full reconciliation failed", e);
            
            ReconciliationResponse response = new ReconciliationResponse(
                    false,
                    "Reconciliation failed: " + e.getMessage()
            );
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Trigger reconciliation for specific date range.
     */
    @PostMapping("/date-range")
    public ResponseEntity<ReconciliationResponse> triggerDateRangeReconciliation(
            @Valid @RequestBody ReconciliationRequest request) {
        
        log.info("Manual date range reconciliation triggered: {} to {}", 
                request.getStartDate(), request.getEndDate());
        
        try {
            ReconciliationResult result = reconciliationService.performDateRangeReconciliation(
                    request.getStartDate(), request.getEndDate());
            
            // ReconciliationResponse response = new ReconciliationResponse(
            //             true,
            //             result.getReconciliationId(),
            //             result.getStatus(),
            //             "Date range reconciliation completed successfully",
            //             result.getStats(),
            //             result.getMismatches()
            //     );
                      ReconciliationResponse response = new ReconciliationResponse(
                    true,
                    result.getReconciliationId(),
                    result.getStatus(),
                    "Date range reconciliation completed successfully",
                    result.getStats(),
                    result.getMismatches(),
                    result.getCorrelationId(),
                    System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Date range reconciliation failed", e);
            
            ReconciliationResponse response = new ReconciliationResponse(
                    false,
                    "Reconciliation failed: " + e.getMessage()
            );
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Trigger reconciliation for specific order.
     */
    @PostMapping("/order/{orderId}")
    public ResponseEntity<ReconciliationResponse> triggerOrderReconciliation(@PathVariable String orderId) {
        log.info("Manual order reconciliation triggered for orderId: {}", orderId);
        
        try {
            ReconciliationResult result = reconciliationService.performOrderReconciliation(orderId);
            
                ReconciliationResponse response = new ReconciliationResponse(
                        true,
                        result.getReconciliationId(),
                        result.getStatus(),
                        "Order reconciliation completed successfully",
                        result.getStats(),
                        result.getMismatches(),
                        result.getCorrelationId(),
                        System.currentTimeMillis()
                );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Order reconciliation failed for orderId: {}", orderId, e);
            
            ReconciliationResponse response = new ReconciliationResponse(
                    false,
                    "Reconciliation failed: " + e.getMessage()
            );
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get reconciliation statistics.
     */
    @GetMapping("/stats")
    public ResponseEntity<ReconciliationResult.ReconciliationStats> getReconciliationStats() {
        log.info("Reconciliation statistics requested");
        
        try {
            ReconciliationResult.ReconciliationStats stats = reconciliationService.getReconciliationStats();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            log.error("Failed to get reconciliation statistics", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Reconciliation service is healthy");
    }
}
