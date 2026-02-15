/**
 * Main Spring Boot application for the Payment Authorization Service.
 * 
 * This service is responsible for evaluating payment authorization rules and publishing
 * appropriate events (PaymentAuthorizedEvent or PaymentRejectedEvent) based on the evaluation.
 * 
 * Key Responsibilities:
 * - Receives PaymentInitiatedEvent via Axon event handling
 * - Uses AuthorizationRulesEngine to evaluate payment risk and business rules
 * - Publishes authorization results as events for downstream services
 * - Integrates with Axon Framework for CQRS event sourcing
 * 
 * Architecture Role: First service in payment processing pipeline
 * Downstream Services: SettlementService, OrderService
 * 
 * Configuration:
 * - Port: 8081 (configurable via application.properties)
 * - Axon Server: localhost:8124 for event distribution
 * - Database: H2 for rule storage and audit trail
 */
package com.payment.platform.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthorizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServiceApplication.class, args);
    }

}
