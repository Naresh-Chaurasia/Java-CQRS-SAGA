package com.payment.platform.order.handler;

import com.payment.platform.order.model.OrderEntity;
import com.payment.platform.order.model.OrderStatus;
import com.payment.platform.order.data.OrderRepository;
import com.payment.platform.order.event.OrderCreatedEvent;
import com.payment.platform.core.events.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@ProcessingGroup("order-group")
@Slf4j
public class OrderEventHandler {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent for order: {}", event.getOrderId());
        
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(event.getOrderId());
        orderEntity.setUserId(event.getUserId());
        orderEntity.setProductId(event.getProductId());
        orderEntity.setProductName(event.getProductName());
        orderEntity.setQuantity(event.getQuantity());
        orderEntity.setUnitPrice(event.getUnitPrice());
        orderEntity.setTotalAmount(event.getTotalAmount());
        orderEntity.setOrderStatus(event.getOrderStatus());
        orderEntity.setCreatedAt(event.getCreatedAt());
        
        orderRepository.save(orderEntity);
        log.info("Order saved to database: {}", event.getOrderId());
    }
    
    @EventHandler
    public void on(PaymentSettledEvent event) {
        log.info("Handling PaymentSettledEvent for order: {}", event.getOrderId());
        
        OrderEntity orderEntity = orderRepository.findByOrderId(event.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found: " + event.getOrderId()));
        
        orderEntity.setOrderStatus(OrderStatus.COMPLETED);
        orderRepository.save(orderEntity);
        
        log.info("Order marked as COMPLETED: {}", event.getOrderId());
        System.out.println("✅ Order completed successfully: " + event.getOrderId() + 
                          ", settlementId: " + event.getSettlementId());
    }
    
    @EventHandler
    public void on(PaymentRejectedEvent event) {
        log.info("Handling PaymentRejectedEvent for order: {}", event.getOrderId());
        
        OrderEntity orderEntity = orderRepository.findByOrderId(event.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found: " + event.getOrderId()));
        
        orderEntity.setOrderStatus(OrderStatus.PAYMENT_FAILED);
        orderRepository.save(orderEntity);
        
        log.warn("Order marked as PAYMENT_FAILED: {}, reason: {}", event.getOrderId(), event.getReason());
        System.out.println("❌ Order payment failed: " + event.getOrderId() + 
                          ", reason: " + event.getReason());
    }
}