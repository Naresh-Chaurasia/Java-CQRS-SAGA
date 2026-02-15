package com.payment.platform.notification.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    
    @NotBlank(message = "Correlation ID is required")
    private String correlationId;
    
    @NotBlank(message = "Event type is required")
    private String eventType;
    
    @NotBlank(message = "Recipient is required")
    private String recipient;
    
    @NotNull(message = "Channel is required")
    private NotificationChannel channel;
    
    private String subject;
    private String content;
    private String template;
    private Map<String, Object> templateData;
    private Map<String, Object> metadata;
}