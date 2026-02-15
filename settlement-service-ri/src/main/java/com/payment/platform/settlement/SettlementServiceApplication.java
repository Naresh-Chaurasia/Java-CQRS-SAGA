package com.payment.platform.settlement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application for the Payment Settlement Service.
 * 
 * This service is responsible for processing authorized payments and settling
 * them with payment providers (banks, payment gateways, etc.).
 * 
 * Key Responsibilities:
 * - Receives PaymentAuthorizedEvent via Axon event handling
 * - Processes payments with external payment providers
 * - Handles retries and payment failures
 * - Publishes PaymentSettledEvent for order fulfillment
 * 
 * Architecture Role: Second service in payment processing pipeline
 * Input: PaymentAuthorizedEvent from AuthorizationService
 * Output: PaymentSettledEvent for OrderService
 * 
 * Configuration:
 * - Port: 8082 (configurable via application.properties)
 * - Axon Server: localhost:8124 for event distribution
 * - Database: H2 for settlement tracking and audit trail
 */
@SpringBootApplication
public class SettlementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SettlementServiceApplication.class, args);
    }
}
