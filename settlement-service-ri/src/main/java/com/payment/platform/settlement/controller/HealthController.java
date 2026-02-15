package com.payment.platform.settlement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check controller for settlement service.
 * 
 * Provides simple health endpoints for monitoring and testing.
 */
@RestController
public class HealthController {
    
    @GetMapping("/health")
    public String health() {
        return "Settlement Service is running on port 8082";
    }
    
    @GetMapping("/status")
    public String status() {
        return "Settlement Service Status: ACTIVE - Ready to process payments";
    }
}
