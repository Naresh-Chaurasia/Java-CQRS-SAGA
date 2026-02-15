package com.payment.platform.notification.service;

import com.payment.platform.notification.model.NotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class ConsoleNotificationService {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public boolean sendConsoleNotification(NotificationEntity notification) {
        try {
            log.info("Sending console notification for: {}", notification.getCorrelationId());
            
            // Create a formatted console output
            System.out.println("\n" + "=".repeat(80));
            System.out.println("🔔 NOTIFICATION CONSOLE OUTPUT");
            System.out.println("=".repeat(80));
            System.out.println("📅 Timestamp: " + notification.getCreatedAt().format(FORMATTER));
            System.out.println("🆔 ID: " + notification.getId());
            System.out.println("🔗 Correlation ID: " + notification.getCorrelationId());
            System.out.println("📋 Event Type: " + notification.getEventType());
            System.out.println("👤 Recipient: " + notification.getRecipient());
            System.out.println("📡 Channel: " + notification.getChannel());
            System.out.println("📊 Status: " + notification.getStatus());
            
            if (notification.getSubject() != null) {
                System.out.println("📝 Subject: " + notification.getSubject());
            }
            
            System.out.println("\n📄 Content:");
            System.out.println("-".repeat(40));
            System.out.println(notification.getContent() != null ? notification.getContent() : "No content provided");
            System.out.println("-".repeat(40));
            
            if (notification.getMetadata() != null) {
                System.out.println("\n📋 Metadata:");
                System.out.println("-".repeat(40));
                System.out.println(notification.getMetadata());
                System.out.println("-".repeat(40));
            }
            
            if (notification.getErrorMessage() != null) {
                System.out.println("\n❌ Error: " + notification.getErrorMessage());
            }
            
            System.out.println("=".repeat(80) + "\n");
            
            log.info("Console notification sent successfully for: {}", notification.getCorrelationId());
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send console notification for {}: {}", 
                     notification.getCorrelationId(), e.getMessage(), e);
            return false;
        }
    }
}