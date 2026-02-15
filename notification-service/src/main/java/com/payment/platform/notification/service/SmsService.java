package com.payment.platform.notification.service;

import com.payment.platform.notification.model.NotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {
    
    public boolean sendSms(NotificationEntity notification) {
        try {
            log.info("Sending SMS to: {}", notification.getRecipient());
            
            // In a real implementation, you would integrate with SMS providers like:
            // - Twilio
            // - AWS SNS
            // - SendGrid
            // - Local SMS gateway
            
            String smsContent = generateSmsContent(notification);
            
            // Simulate SMS sending
            System.out.println("📱 SMS NOTIFICATION");
            System.out.println("==================");
            System.out.println("To: " + notification.getRecipient());
            System.out.println("Message: " + smsContent);
            System.out.println("Event: " + notification.getEventType());
            System.out.println("Correlation ID: " + notification.getCorrelationId());
            System.out.println("==================");
            
            // Simulate success
            log.info("SMS sent successfully to: {}", notification.getRecipient());
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", notification.getRecipient(), e.getMessage(), e);
            return false;
        }
    }
    
    private String generateSmsContent(NotificationEntity notification) {
        StringBuilder content = new StringBuilder();
        
        // SMS messages should be concise
        switch (notification.getEventType()) {
            case "payment.settled":
                content.append("Payment of $XX.XX has been successfully processed. Ref: ")
                       .append(notification.getCorrelationId());
                break;
                
            case "payment.rejected":
                content.append("Payment was declined. Please contact support. Ref: ")
                       .append(notification.getCorrelationId());
                break;
                
            case "order.completed":
                content.append("Your order has been completed successfully! Ref: ")
                       .append(notification.getCorrelationId());
                break;
                
            default:
                content.append("Payment update: ").append(notification.getEventType())
                       .append(". Ref: ").append(notification.getCorrelationId());
                break;
        }
        
        return content.toString();
    }
}