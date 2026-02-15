package com.payment.platform.notification.service;

import com.payment.platform.notification.data.NotificationRepository;
import com.payment.platform.notification.model.NotificationEntity;
import com.payment.platform.notification.model.NotificationRequest;
import com.payment.platform.notification.model.NotificationChannel;
import com.payment.platform.notification.model.NotificationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Simplified notification service using file-based logging.
 * Perfect for sample assignment - demonstrates core concepts without external dependencies.
 */
@Service
@Transactional
@Slf4j
public class SimpleNotificationService implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final SimpleNotificationProcessor notificationProcessor;
    private final FileNotificationService fileNotificationService;
    
    @Autowired
    public SimpleNotificationService(NotificationRepository notificationRepository,
                                   SimpleNotificationProcessor notificationProcessor,
                                   FileNotificationService fileNotificationService) {
        this.notificationRepository = notificationRepository;
        this.notificationProcessor = notificationProcessor;
        this.fileNotificationService = fileNotificationService;
    }
    
    @Override
    public NotificationEntity sendNotification(NotificationRequest request) {
        log.info("Sending notification request (file-based): correlationId={}, eventType={}, channel={}", 
                request.getCorrelationId(), request.getEventType(), request.getChannel());
        
        // Create notification entity
        NotificationEntity notification = new NotificationEntity();
        notification.setCorrelationId(request.getCorrelationId());
        notification.setEventType(request.getEventType());
        notification.setRecipient(request.getRecipient());
        notification.setChannel(request.getChannel());
        notification.setSubject(request.getSubject());
        notification.setContent(request.getContent());
        notification.setTemplate(request.getTemplate());
        notification.setMetadata(request.getMetadata() != null ? request.getMetadata().toString() : null);
        notification.setStatus(NotificationStatus.PENDING);
        
        // Save notification
        notification = notificationRepository.save(notification);
        
        // Process notification using file-based approach
        boolean success = notificationProcessor.processNotification(notification);
        
        // Update status and save
        notification = notificationRepository.save(notification);
        
        log.info("File-based notification processing completed: {} -> {}", 
                notification.getId(), notification.getStatus());
        
        return notification;
    }
    
    @Override
    public NotificationEntity sendNotification(String correlationId, String eventType, String recipient, Object eventData) {
        log.info("Auto-sending notification (file-based): correlationId={}, eventType={}, recipient={}", 
                correlationId, eventType, recipient);
        
        // Determine channel based on event type
        NotificationChannel channel = notificationProcessor.determineChannel(eventType, null);
        
        // Generate content based on event type and data
        String subject = generateSubject(eventType, eventData);
        String content = generateContent(eventType, eventData);
        
        NotificationRequest request = new NotificationRequest();
        request.setCorrelationId(correlationId);
        request.setEventType(eventType);
        request.setRecipient(recipient);
        request.setChannel(channel);
        request.setSubject(subject);
        request.setContent(content);
        
        return sendNotification(request);
    }
    
    @Override
    public void retryFailedNotifications() {
        log.info("Starting retry process for failed notifications (file-based)");
        
        List<NotificationEntity> failedNotifications = 
            notificationRepository.findFailedNotificationsForRetry(NotificationStatus.FAILED, 3);
        
        int retryCount = 0;
        int successCount = 0;
        
        for (NotificationEntity notification : failedNotifications) {
            retryCount++;
            
            log.info("Retrying notification (file-based): {}", notification.getId());
            
            notification.setStatus(NotificationStatus.RETRYING);
            notification.setRetryCount(notification.getRetryCount() + 1);
            notificationRepository.save(notification);
            
            boolean success = notificationProcessor.processNotification(notification);
            
            if (success) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                successCount++;
                log.info("Retry successful for notification: {}", notification.getId());
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                log.warn("Retry failed for notification: {}", notification.getId());
            }
            
            notificationRepository.save(notification);
        }
        
        log.info("File-based retry process completed: {} retried, {} successful", retryCount, successCount);
    }
    
    @Override
    public NotificationEntity getNotification(String id) {
        return notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
    }
    
    @Override
    public List<NotificationEntity> getNotificationsByCorrelationId(String correlationId) {
        return notificationRepository.findByCorrelationId(correlationId);
    }
    
    @Override
    public List<NotificationEntity> getNotificationsByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }
    
    @Override
    public NotificationStatistics getStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last24Hours = now.minusHours(24);
        LocalDateTime lastHour = now.minusHours(1);
        
        Long totalNotifications = notificationRepository.count();
        Long sentNotifications = notificationRepository.countByStatusSince(NotificationStatus.SENT, last24Hours);
        Long failedNotifications = notificationRepository.countByStatusSince(NotificationStatus.FAILED, last24Hours);
        Long pendingNotifications = notificationRepository.countByStatusSince(NotificationStatus.PENDING, last24Hours);
        Long notificationsInLastHour = notificationRepository.countByStatusSince(NotificationStatus.SENT, lastHour);
        Long notificationsInLast24Hours = sentNotifications;
        
        NotificationStatistics statistics = new NotificationStatistics();
        statistics.setGeneratedAt(now);
        statistics.setTotalNotifications(totalNotifications);
        statistics.setSentNotifications(sentNotifications);
        statistics.setFailedNotifications(failedNotifications);
        statistics.setPendingNotifications(pendingNotifications);
        statistics.setNotificationsInLastHour(notificationsInLastHour);
        statistics.setNotificationsInLast24Hours(notificationsInLast24Hours);
        
        // Add file-based statistics
        FileNotificationService.NotificationFileStatistics fileStats = fileNotificationService.getFileStatistics();
        statistics.setFileStatistics(fileStats);
        
        return statistics;
    }
    
    @Override
    public void markAsDelivered(String notificationId) {
        NotificationEntity notification = getNotification(notificationId);
        notification.setStatus(NotificationStatus.DELIVERED);
        notification.setDeliveredAt(LocalDateTime.now());
        notificationRepository.save(notification);
        
        log.info("Notification marked as delivered (file-based): {}", notificationId);
    }
    
    @Override
    public void markAsFailed(String notificationId, String errorMessage) {
        NotificationEntity notification = getNotification(notificationId);
        notification.setStatus(NotificationStatus.FAILED);
        notification.setErrorMessage(errorMessage);
        notification.setRetryCount(notification.getRetryCount() + 1);
        notificationRepository.save(notification);
        
        log.error("Notification marked as failed (file-based): {}, error: {}", notificationId, errorMessage);
    }
    
    // Additional file-specific methods
    
    /**
     * Get file-based notification statistics
     */
    public FileNotificationService.NotificationFileStatistics getFileStatistics() {
        return fileNotificationService.getFileStatistics();
    }
    
    /**
     * Get log file information
     */
    public String getLogDirectoryInfo() {
        FileNotificationService.NotificationFileStatistics stats = fileNotificationService.getFileStatistics();
        return String.format("Log Directory: %s, Files: %d, Total Size: %s", 
                stats.getLogDirectory(), stats.getTotalFiles(), stats.getFormattedSize());
    }
    
    private String generateSubject(String eventType, Object eventData) {
        switch (eventType) {
            case "payment.settled":
                return "Payment Successful";
            case "payment.rejected":
                return "Payment Declined";
            case "payment.initiated":
                return "Payment Initiated";
            case "order.completed":
                return "Order Completed";
            case "order.payment_failed":
                return "Payment Failed";
            default:
                return "Payment Notification";
        }
    }
    
    private String generateContent(String eventType, Object eventData) {
        StringBuilder content = new StringBuilder();
        
        switch (eventType) {
            case "payment.settled":
                content.append("Your payment has been successfully processed and settled.\n\n");
                content.append("Thank you for your payment!");
                break;
                
            case "payment.rejected":
                content.append("Your payment was declined.\n\n");
                content.append("Please review your payment details and try again.\n");
                content.append("If the problem persists, please contact customer support.");
                break;
                
            case "payment.initiated":
                content.append("Your payment has been initiated and is being processed.\n\n");
                content.append("You will receive another notification once the payment is complete.");
                break;
                
            case "order.completed":
                content.append("Your order has been completed successfully!\n\n");
                content.append("Thank you for your purchase. Your order is now ready for delivery.");
                break;
                
            case "order.payment_failed":
                content.append("There was an issue processing your payment for this order.\n\n");
                content.append("Please update your payment information and try again.");
                break;
                
            default:
                content.append("There's an update regarding your payment: ").append(eventType);
                break;
        }
        
        if (eventData != null) {
            content.append("\n\nAdditional Information:\n");
            content.append(eventData.toString());
        }
        
        return content.toString();
    }
}