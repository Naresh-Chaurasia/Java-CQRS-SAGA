package com.payment.platform.notification.service;

import com.payment.platform.notification.model.NotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;

@Service
@Slf4j
public class WebhookService {
    
    private final RestTemplate restTemplate;
    
    public WebhookService() {
        this.restTemplate = new RestTemplate();
    }
    
    public boolean sendWebhook(NotificationEntity notification) {
        try {
            log.info("Sending webhook notification for: {}", notification.getCorrelationId());
            
            // In a real implementation, you would:
            // 1. Get webhook URL from configuration or database
            // 2. Authenticate with the webhook endpoint
            // 3. Send properly formatted JSON payload
            // 4. Handle retries and failures
            
            String webhookUrl = getWebhookUrl(notification);
            if (webhookUrl == null) {
                log.warn("No webhook URL configured for notification: {}", notification.getId());
                return sendWebhookFallback(notification);
            }
            
            Map<String, Object> webhookPayload = createWebhookPayload(notification);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Event-Type", notification.getEventType());
            headers.set("X-Correlation-ID", notification.getCorrelationId());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(webhookPayload, headers);
            
            // Send webhook
            restTemplate.postForEntity(webhookUrl, entity, String.class);
            
            log.info("Webhook sent successfully to: {}", webhookUrl);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send webhook for {}: {}", 
                     notification.getCorrelationId(), e.getMessage(), e);
            return sendWebhookFallback(notification);
        }
    }
    
    private String getWebhookUrl(NotificationEntity notification) {
        // In a real implementation, this would:
        // 1. Check configuration for default webhook URL
        // 2. Look up webhook URL based on event type
        // 3. Check metadata for custom webhook URL
        
        // For demo, return null to trigger fallback
        return null;
    }
    
    private Map<String, Object> createWebhookPayload(NotificationEntity notification) {
        return Map.of(
            "id", notification.getId(),
            "eventType", notification.getEventType(),
            "correlationId", notification.getCorrelationId(),
            "recipient", notification.getRecipient(),
            "subject", notification.getSubject(),
            "content", notification.getContent(),
            "timestamp", notification.getCreatedAt().toString(),
            "metadata", notification.getMetadata()
        );
    }
    
    private boolean sendWebhookFallback(NotificationEntity notification) {
        // Fallback to console output when webhook is not configured
        System.out.println("🔗 WEBHOOK NOTIFICATION");
        System.out.println("========================");
        System.out.println("Event: " + notification.getEventType());
        System.out.println("Correlation ID: " + notification.getCorrelationId());
        System.out.println("Recipient: " + notification.getRecipient());
        System.out.println("Subject: " + notification.getSubject());
        System.out.println("Content: " + notification.getContent());
        System.out.println("Timestamp: " + notification.getCreatedAt());
        
        if (notification.getMetadata() != null) {
            System.out.println("Metadata: " + notification.getMetadata());
        }
        
        System.out.println("========================");
        return true;
    }
}