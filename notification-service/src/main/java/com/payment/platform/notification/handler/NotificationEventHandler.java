package com.payment.platform.notification.handler;

import com.payment.platform.notification.service.NotificationService;
import com.payment.platform.core.events.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ProcessingGroup("notification-group")
@Slf4j
public class NotificationEventHandler {
    
    @Autowired
    private NotificationService notificationService;
    
    @EventHandler
    public void on(PaymentSettledEvent event) {
        log.info("Processing PaymentSettledEvent for notification: {}", event.getPaymentId());
        
        // Create notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("paymentId", event.getPaymentId());
        notificationData.put("orderId", event.getOrderId());
        notificationData.put("settlementId", event.getSettlementId());
        notificationData.put("settlementDate", event.getSettlementDate());
        notificationData.put("amount", "Amount from payment"); // In real implementation, get from payment data

        log.info("Notifications sent for settled payment1: {}", event.getPaymentId());
        
        // Send notification to user (assuming user ID can be derived from order ID)
        String recipient = "user@" + event.getOrderId() + ".com"; // Simplified recipient logic
        
        // notificationService.sendNotification(
        //     event.getPaymentId(), 
        //     "payment.settled", 
        //     recipient, 
        //     notificationData
        // );

        // log.info("Notifications sent for settled payment2: {}", event.getPaymentId());
        
        // // Send merchant notification
        // String merchantRecipient = "merchant@payment-platform.com";
        // notificationService.sendNotification(
        //     event.getPaymentId() + "-merchant", 
        //     "payment.settled", 
        //     merchantRecipient, 
        //     notificationData
        // );
        
        log.info("Notifications sent for settled payment3: {}", event.getPaymentId());
    }
    
    //@EventHandler
    public void on(PaymentRejectedEvent event) {
        log.info("Processing PaymentRejectedEvent for notification: {}", event.getPaymentId());
        
        // Create notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("paymentId", event.getPaymentId());
        notificationData.put("orderId", event.getOrderId());
        notificationData.put("reason", event.getRejectionReason());
        notificationData.put("errorCode", event.getErrorCode());
        notificationData.put("timestamp", event.getTimestamp());
        
        // Send notification to user
        String recipient = "user@" + event.getOrderId() + ".com"; // Simplified recipient logic
        
        notificationService.sendNotification(
            event.getPaymentId(), 
            "payment.rejected", 
            recipient, 
            notificationData
        );
        
        // Send alert to support team
        String supportRecipient = "support@payment-platform.com";
        notificationService.sendNotification(
            event.getPaymentId() + "-support", 
            "payment.rejected", 
            supportRecipient, 
            notificationData
        );
        
        log.info("Notifications sent for rejected payment: {}", event.getPaymentId());
    }
    
    //@EventHandler
    public void on(PaymentAuthorizedEvent event) {
        log.info("Processing PaymentAuthorizedEvent for notification: {}", event.getPaymentId());
        
        // Create notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("paymentId", event.getPaymentId());
        notificationData.put("orderId", event.getOrderId());
        notificationData.put("authorizationCode", event.getAuthorizationCode());
        notificationData.put("riskScore", event.getRiskScore());
        notificationData.put("amount", event.getAmount());
        
        // Send notification to user about authorization
        String recipient = "user@" + event.getOrderId() + ".com"; // Simplified recipient logic
        
        notificationService.sendNotification(
            event.getPaymentId(), 
            "payment.authorized", 
            recipient, 
            notificationData
        );
        
        log.info("Authorization notification sent: {}", event.getPaymentId());
    }
    
    //@EventHandler
    public void on(PaymentInitiatedEvent event) {
        log.info("Processing PaymentInitiatedEvent for notification: {}", event.getPaymentId());
        
        // Create notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("paymentId", event.getPaymentId());
        notificationData.put("orderId", event.getOrderId());
        notificationData.put("amount", event.getAmount());
        notificationData.put("currency", event.getCurrency());
        notificationData.put("userId", event.getUserId());
        notificationData.put("merchantId", event.getMerchantId());
        notificationData.put("paymentMethod", event.getPaymentMethod());
        
        // Send notification to user about payment initiation
        String recipient = "user@" + event.getUserId() + ".com"; // Simplified recipient logic
        
        notificationService.sendNotification(
            event.getPaymentId(), 
            "payment.initiated", 
            recipient, 
            notificationData
        );
        
        log.info("Payment initiation notification sent: {}", event.getPaymentId());
    }
    
    // Handle custom order events if they exist
   // @EventHandler
    public void on(Object event) {
        // Generic event handler for any other events
        if (event.getClass().getSimpleName().contains("Order")) {
            log.info("Processing order event for notification: {}", event.getClass().getSimpleName());
            
            // Extract basic information from the event
            String eventType = event.getClass().getSimpleName();
            String correlationId = eventType + "-" + System.currentTimeMillis();
            
            // Send generic order notification
            notificationService.sendNotification(
                correlationId,
                "order." + eventType.toLowerCase(),
                "admin@payment-platform.com",
                event
            );
        }
    }
}