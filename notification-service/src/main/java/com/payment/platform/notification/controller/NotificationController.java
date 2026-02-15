package com.payment.platform.notification.controller;

import com.payment.platform.notification.model.NotificationEntity;
import com.payment.platform.notification.model.NotificationRequest;
import com.payment.platform.notification.model.NotificationChannel;
import com.payment.platform.notification.model.NotificationStatus;
import com.payment.platform.notification.service.NotificationService;
import com.payment.platform.notification.service.NotificationStatistics;
import com.payment.platform.notification.service.UiNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UiNotificationService uiNotificationService;
    
    @PostMapping
    public ResponseEntity<NotificationEntity> sendNotification(@Valid @RequestBody NotificationRequest request) {
        log.info("REST request to send notification: {}", request.getCorrelationId());
        
        NotificationEntity notification = notificationService.sendNotification(request);
        return ResponseEntity.ok(notification);
    }
    
    @PostMapping("/simple")
    public ResponseEntity<NotificationEntity> sendSimpleNotification(
            @RequestParam String correlationId,
            @RequestParam String eventType,
            @RequestParam String recipient,
            @RequestParam(required = false) String message) {
        
        log.info("REST request to send simple notification: {}", correlationId);
        
        Object eventData = message != null ? message : "Simple notification message";
        NotificationEntity notification = notificationService.sendNotification(
            correlationId, eventType, recipient, eventData);
        
        return ResponseEntity.ok(notification);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NotificationEntity> getNotification(@PathVariable String id) {
        try {
            NotificationEntity notification = notificationService.getNotification(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<NotificationEntity>> getNotificationsByCorrelationId(@PathVariable String correlationId) {
        List<NotificationEntity> notifications = notificationService.getNotificationsByCorrelationId(correlationId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationEntity>> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        List<NotificationEntity> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping
    public ResponseEntity<List<NotificationEntity>> getAllNotifications() {
        List<NotificationEntity> notifications = notificationService.getNotificationsByStatus(null); // This would need implementation
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<NotificationStatistics> getStatistics() {
        NotificationStatistics statistics = notificationService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @PostMapping("/retry")
    public ResponseEntity<String> retryFailedNotifications() {
        log.info("Manual retry request for failed notifications");
        
        notificationService.retryFailedNotifications();
        
        return ResponseEntity.ok("Retry process initiated for failed notifications");
    }
    
    @PutMapping("/{id}/delivered")
    public ResponseEntity<String> markAsDelivered(@PathVariable String id) {
        try {
            notificationService.markAsDelivered(id);
            return ResponseEntity.ok("Notification marked as delivered: " + id);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/failed")
    public ResponseEntity<String> markAsFailed(@PathVariable String id, @RequestBody String errorMessage) {
        try {
            notificationService.markAsFailed(id, errorMessage);
            return ResponseEntity.ok("Notification marked as failed: " + id);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // UI-specific endpoints
    @GetMapping("/ui/{recipient}")
    public ResponseEntity<Map<String, Object>> getUiNotifications(@PathVariable String recipient) {
        Map<String, Object> notifications = uiNotificationService.getUiNotifications(recipient);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/ui/notification/{id}")
    public ResponseEntity<Map<String, Object>> getUiNotification(@PathVariable String id) {
        Map<String, Object> notification = uiNotificationService.getUiNotification(id);
        if (notification != null) {
            return ResponseEntity.ok(notification);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Test endpoints
    @GetMapping("/test")
    public ResponseEntity<String> testNotification() {
        return ResponseEntity.ok("Notification Service is running! Ready to process payment events.");
    }
    
    @PostMapping("/test/payment-settled")
    public ResponseEntity<String> testPaymentSettledNotification() {
        log.info("Testing payment settled notification");
        
        String correlationId = "test-payment-settled-" + System.currentTimeMillis();
        String recipient = "test@example.com";
        
        Map<String, Object> testData = Map.of(
            "paymentId", "test-payment-123",
            "orderId", "test-order-456",
            "settlementId", "settlement-789",
            "amount", "$100.00",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        
        notificationService.sendNotification(correlationId, "payment.settled", recipient, testData);
        
        return ResponseEntity.ok("Payment settled notification test sent: " + correlationId);
    }
    
    @PostMapping("/test/payment-rejected")
    public ResponseEntity<String> testPaymentRejectedNotification() {
        log.info("Testing payment rejected notification");
        
        String correlationId = "test-payment-rejected-" + System.currentTimeMillis();
        String recipient = "test@example.com";
        
        Map<String, Object> testData = Map.of(
            "paymentId", "test-payment-123",
            "orderId", "test-order-456",
            "reason", "Insufficient funds",
            "errorCode", "CARD_DECLINED",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        
        notificationService.sendNotification(correlationId, "payment.rejected", recipient, testData);
        
        return ResponseEntity.ok("Payment rejected notification test sent: " + correlationId);
    }
    
    @PostMapping("/test/all-channels")
    public ResponseEntity<String> testAllChannels() {
        log.info("Testing all notification channels");
        
        String correlationId = "test-all-channels-" + System.currentTimeMillis();
        String recipient = "test@example.com";
        String message = "This is a test message for all notification channels";
        
        for (NotificationChannel channel : NotificationChannel.values()) {
            NotificationRequest request = new NotificationRequest();
            request.setCorrelationId(correlationId + "-" + channel.name());
            request.setEventType("test.notification");
            request.setRecipient(recipient);
            request.setChannel(channel);
            request.setSubject("Test Notification - " + channel.name());
            request.setContent(message);
            
            notificationService.sendNotification(request);
        }
        
        return ResponseEntity.ok("Test notifications sent to all channels: " + correlationId);
    }
}