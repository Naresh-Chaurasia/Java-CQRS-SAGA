package com.payment.platform.notification.service;

import com.payment.platform.notification.model.NotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UiNotificationService {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // In-memory store for UI notifications (in production, use Redis or database)
    private final Map<String, Map<String, Object>> uiNotifications = new ConcurrentHashMap<>();
    
    public boolean sendUiNotification(NotificationEntity notification) {
        try {
            log.info("Sending UI notification for: {}", notification.getCorrelationId());
            
            // Create UI notification payload
            Map<String, Object> uiNotification = new HashMap<>();
            uiNotification.put("id", notification.getId());
            uiNotification.put("correlationId", notification.getCorrelationId());
            uiNotification.put("eventType", notification.getEventType());
            uiNotification.put("recipient", notification.getRecipient());
            uiNotification.put("subject", notification.getSubject() != null ? notification.getSubject() : "Payment Notification");
            uiNotification.put("content", notification.getContent() != null ? notification.getContent() : generateUiContent(notification));
            uiNotification.put("timestamp", notification.getCreatedAt().format(FORMATTER));
            uiNotification.put("status", notification.getStatus().toString());
            uiNotification.put("channel", notification.getChannel().toString());
            uiNotification.put("severity", getSeverity(notification.getEventType()));
            uiNotification.put("actionable", isActionable(notification.getEventType()));
            
            // Store notification for UI retrieval
            uiNotifications.put(notification.getId(), uiNotification);
            
            // Display in console (simulating UI display)
            displayUiNotification(uiNotification);
            
            // In a real implementation, you would:
            // 1. Send via WebSocket to connected clients
            // 2. Store in Redis for real-time access
            // 3. Push to mobile app via push notification service
            // 4. Update UI components in real-time
            
            log.info("UI notification sent successfully for: {}", notification.getCorrelationId());
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send UI notification for {}: {}", 
                     notification.getCorrelationId(), e.getMessage(), e);
            return false;
        }
    }
    
    public Map<String, Object> getUiNotifications(String recipient) {
        // In production, this would query Redis or database
        return Map.of("notifications", uiNotifications.values().stream()
            .filter(n -> n.get("recipient").equals(recipient))
            .toList());
    }
    
    public Map<String, Object> getUiNotification(String notificationId) {
        return uiNotifications.get(notificationId);
    }
    
    private void displayUiNotification(Map<String, Object> notification) {
        System.out.println("\n🖥️  UI NOTIFICATION DISPLAY");
        System.out.println("=".repeat(60));
        System.out.println("🔔 " + notification.get("subject"));
        System.out.println("📅 " + notification.get("timestamp"));
        System.out.println("👤 " + notification.get("recipient"));
        System.out.println("📋 " + notification.get("eventType"));
        System.out.println("⚠️  Severity: " + notification.get("severity"));
        
        System.out.println("\n📄 Message:");
        System.out.println("-".repeat(40));
        System.out.println(notification.get("content"));
        System.out.println("-".repeat(40));
        
        if ((Boolean) notification.get("actionable")) {
            System.out.println("\n🔗 Action Required: View payment details");
        }
        
        System.out.println("=".repeat(60) + "\n");
    }
    
    private String generateUiContent(NotificationEntity notification) {
        switch (notification.getEventType()) {
            case "payment.settled":
                return "Your payment has been successfully processed and settled.";
            case "payment.rejected":
                return "Your payment was declined. Please review your payment details and try again.";
            case "order.completed":
                return "Your order has been completed successfully! Thank you for your purchase.";
            case "order.payment_failed":
                return "There was an issue processing your payment. Please update your payment information.";
            default:
                return "There's an update regarding your payment: " + notification.getEventType();
        }
    }
    
    private String getSeverity(String eventType) {
        switch (eventType) {
            case "payment.settled":
            case "order.completed":
                return "success";
            case "payment.rejected":
            case "order.payment_failed":
                return "error";
            case "payment.initiated":
                return "info";
            default:
                return "info";
        }
    }
    
    private boolean isActionable(String eventType) {
        return eventType.equals("payment.rejected") || eventType.equals("order.payment_failed");
    }
}