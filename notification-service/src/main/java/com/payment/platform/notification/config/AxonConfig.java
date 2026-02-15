package com.payment.platform.notification.config;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.messaging.StreamableMessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Axon Framework configuration for notification service.
 * 
 * Configures EventBus for event processing.
 * Provides primary EventBus bean to resolve dependency injection issues.
 */
// @Configuration
// public class AxonConfig {
    
//     /**
//      * Primary EventBus bean for event processing.
//      * This bean is required by Axon framework for event handling.
//      */
//     @Bean
//     @Primary
//     public StreamableMessageSource eventBus() {
//         return (StreamableMessageSource) SimpleEventBus.builder()
//                 .build();
//     }
// }
