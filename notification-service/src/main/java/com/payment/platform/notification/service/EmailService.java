package com.payment.platform.notification.service;

import com.payment.platform.notification.model.NotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
@Slf4j
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Autowired(required = false)
    private TemplateEngine templateEngine;
    
    public boolean sendEmail(NotificationEntity notification) {
        try {
            if (mailSender == null) {
                log.warn("Mail sender not configured, falling back to console output");
                return sendEmailFallback(notification);
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(notification.getRecipient());
            helper.setSubject(notification.getSubject() != null ? notification.getSubject() : "Payment Notification");
            
            // Use template if specified, otherwise use content
            String emailContent;
            if (notification.getTemplate() != null && templateEngine != null) {
                Context context = new Context();
                // Parse metadata for template variables
                if (notification.getMetadata() != null) {
                    // Simple JSON parsing for template data
                    try {
                        Map<String, Object> templateData = parseMetadata(notification);
                        templateData.forEach(context::setVariable);
                    } catch (Exception e) {
                        log.warn("Failed to parse metadata for template: {}", e.getMessage());
                    }
                }
                emailContent = templateEngine.process(notification.getTemplate(), context);
            } else {
                emailContent = notification.getContent() != null ? 
                    notification.getContent() : generateDefaultContent(notification);
            }
            
            helper.setText(emailContent, true);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", notification.getRecipient());
            return true;
            
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", notification.getRecipient(), e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error sending email: {}", e.getMessage(), e);
            return false;
        }
    }
    
    private boolean sendEmailFallback(NotificationEntity notification) {
        // Fallback to console output when email is not configured
        System.out.println("📧 EMAIL NOTIFICATION");
        System.out.println("==================");
        System.out.println("To: " + notification.getRecipient());
        System.out.println("Subject: " + (notification.getSubject() != null ? notification.getSubject() : "Payment Notification"));
        System.out.println("Content: " + (notification.getContent() != null ? notification.getContent() : generateDefaultContent(notification)));
        System.out.println("Event: " + notification.getEventType());
        System.out.println("Correlation ID: " + notification.getCorrelationId());
        System.out.println("==================");
        return true;
    }
    
    private String generateDefaultContent(NotificationEntity notificationEntity) {
        StringBuilder content = new StringBuilder();
        content.append("Payment Notification\n\n");
        content.append("Event Type: ").append(notificationEntity.getEventType()).append("\n");
        content.append("Correlation ID: ").append(notificationEntity.getCorrelationId()).append("\n");
        content.append("Status: ").append(notificationEntity.getStatus()).append("\n");
        content.append("Created At: ").append(notificationEntity.getCreatedAt()).append("\n");
        
        if (notificationEntity.getMetadata() != null) {
            content.append("\nAdditional Information:\n");
            content.append(notificationEntity.getMetadata());
        }
        
        return content.toString();
    }
    
    private Map<String, Object> parseMetadata(NotificationEntity notificationEntity) {
        // Simple metadata parsing - in production, use proper JSON parser
        // This is a simplified version for demonstration
        return Map.of(
            "eventType", notificationEntity.getEventType(),
            "correlationId", notificationEntity.getCorrelationId(),
            "createdAt", notificationEntity.getCreatedAt().toString()
        );
    }
}