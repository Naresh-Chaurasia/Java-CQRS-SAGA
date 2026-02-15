package com.payment.platform.order.command;

import com.payment.platform.order.model.OrderStatus;
import com.payment.platform.order.event.OrderCreatedEvent;
import com.payment.platform.order.event.OrderCompletedEvent;
import com.payment.platform.order.event.OrderPaymentFailedEvent;
import com.payment.platform.core.events.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Aggregate
@Slf4j
public class OrderAggregate {
    
    @AggregateIdentifier
    private String orderId;
    private String userId;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    
    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        log.info("Creating order: {}", command.getOrderId());
        
        OrderCreatedEvent event = new OrderCreatedEvent(
            command.getOrderId(),
            command.getUserId(),
            command.getProductId(),
            command.getProductName(),
            command.getQuantity(),
            command.getUnitPrice(),
            command.getTotalAmount(),
            OrderStatus.CREATED,
            LocalDateTime.now()
        );
        
        AggregateLifecycle.apply(event);
    }
    
    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        log.info("Order created event received: {}", event.getOrderId());
        
        this.orderId = event.getOrderId();
        this.userId = event.getUserId();
        this.productId = event.getProductId();
        this.productName = event.getProductName();
        this.quantity = event.getQuantity();
        this.unitPrice = event.getUnitPrice();
        this.totalAmount = event.getTotalAmount();
        this.orderStatus = event.getOrderStatus();
        this.createdAt = event.getCreatedAt();
    }
    
    @EventSourcingHandler
    public void on(PaymentSettledEvent event) {
        log.info("Payment settled event received for order: {}", event.getOrderId());
        
        if (this.orderId.equals(event.getOrderId())) {
            this.orderStatus = OrderStatus.COMPLETED;
            
            // Publish order completed event
            OrderCompletedEvent completedEvent = new OrderCompletedEvent(
                this.orderId,
                this.userId,
                event.getPaymentId(),
                event.getSettlementId(),
                LocalDateTime.now()
            );
            
            AggregateLifecycle.apply(completedEvent);
        }
    }
    
    @EventSourcingHandler
    public void on(PaymentRejectedEvent event) {
        log.info("Payment rejected event received for order: {}", event.getOrderId());
        
        if (this.orderId.equals(event.getOrderId())) {
            this.orderStatus = OrderStatus.PAYMENT_FAILED;
            
            // Publish order payment failed event
            OrderPaymentFailedEvent failedEvent = new OrderPaymentFailedEvent(
                this.orderId,
                this.userId,
                event.getReason(),
                LocalDateTime.now()
            );
            
            AggregateLifecycle.apply(failedEvent);
        }
    }
}