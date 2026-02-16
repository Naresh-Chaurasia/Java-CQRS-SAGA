package com.payment.platform.reconciliation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Payment Reconciliation Service
 * 
 * This service provides:
 * - Scheduled reconciliation jobs to compare payments vs settlements
 * - Event-driven reconciliation via Axon Framework
 * - REST API for manual reconciliation triggers and reports
 * - Observability with structured logging and metrics
 */
@SpringBootApplication
@EnableScheduling
public class ReconciliationServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ReconciliationServiceApplication.class, args);
    }
}
