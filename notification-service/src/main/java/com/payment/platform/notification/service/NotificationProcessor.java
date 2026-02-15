package com.payment.platform.notification.service;

import com.payment.platform.notification.model.NotificationEntity;
import com.payment.platform.notification.model.NotificationChannel;
import com.payment.platform.notification.model.NotificationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Unified notification processor that handles different channels
 */
@Service
@Slf4j
public class NotificationProcessor {
    
    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushService;
    private final WebhookService webhookService;
    private final ConsoleNotificationService consoleService;
    private final UiNotificationService uiService;
    
    public NotificationProcessor(EmailService emailService, 
                               SmsService smsService,
                               PushNotificationService pushService,
                               WebhookService webhookService,
                               ConsoleNotificationService consoleService,
                               UiNotificationService uiService) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.pushService = pushService;
        this.webhookService = webhookService;
        this.consoleService = consoleService;
        this.uiService = uiService;
    }
    
    /**
     * Process notification through appropriate channel
     */
    public boolean processNotification(NotificationEntity notification) {
        try {
            log.info("Processing notification {} via channel: {}", 
                    notification.getId(), notification.getChannel());
            
            boolean success = false;
            
            switch (notification.getChannel()) {
                case EMAIL:
                    success = emailService.sendEmail(notification);
                    break;
                    
                case SMS:
                    success = smsService.sendSms(notification);
                    break;
                    
                case PUSH:
                    success = pushService.sendPushNotification(notification);
                    break;
                    
                case WEBHOOK:
                    success = webhookService.sendWebhook(notification);
                    break;
                    
                case CONSOLE:
                    success = consoleService.sendConsoleNotification(notification);
                    break;
                    
                case UI:
                    success = uiService.sendUiNotification(notification);
                    break;
                    
                default:
                    log.error("Unsupported notification channel: {}", notification.getChannel());
                    return false;
            }
            
            if (success) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                log.info("Notification {} sent successfully", notification.getId());
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setErrorMessage("Channel processing failed");
                log.error("Notification {} processing failed", notification.getId());
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("Error processing notification {}: {}", notification.getId(), e.getMessage(), e);
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            return false;
        }
    }
    
    /**
     * Determine appropriate channel based on event type and preferences
     */
    public NotificationChannel determineChannel(String eventType, Map<String, Object> metadata) {
        // Default channel mapping based on event type
        switch (eventType) {
            case "payment.settled":
                return NotificationChannel.EMAIL;
                
            case "payment.rejected":
                return NotificationChannel.EMAIL;
                
            case "payment.initiated":
                return NotificationChannel.UI;
                
            case "order.completed":
                return NotificationChannel.EMAIL;
                
            case "order.payment_failed":
                return NotificationChannel.EMAIL;
                
            case "system.alert":
                return NotificationChannel.CONSOLE;
                
            default:
                // Check metadata for channel preference
                if (metadata != null && metadata.containsKey("preferredChannel")) {
                    String preferredChannel = (String) metadata.get("preferredChannel");
                    try {
                        return NotificationChannel.valueOf(preferredChannel.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        log.warn("Invalid preferred channel: {}, using default", preferredChannel);
                    }
                }
                
                // Default to console for unknown events
                return NotificationChannel.CONSOLE;
        }
    }
}