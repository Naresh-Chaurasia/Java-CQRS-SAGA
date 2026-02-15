package com.payment.platform.order.controller;

import com.payment.platform.order.command.CreateOrderCommand;
import com.payment.platform.order.model.OrderEntity;
import com.payment.platform.order.data.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {
    
    @Autowired
    private CommandGateway commandGateway;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @PostMapping
    public ResponseEntity<String> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());
        
        String orderId = UUID.randomUUID().toString();
        BigDecimal totalAmount = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        
        CreateOrderCommand command = new CreateOrderCommand(
            orderId,
            request.getUserId(),
            request.getProductId(),
            request.getProductName(),
            request.getQuantity(),
            request.getUnitPrice(),
            totalAmount
        );
        
        commandGateway.sendAndWait(command);
        
        return ResponseEntity.ok("Order created successfully: " + orderId);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderEntity> getOrder(@PathVariable String orderId) {
        return orderRepository.findByOrderId(orderId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderEntity>> getOrdersByUser(@PathVariable String userId) {
        List<OrderEntity> orders = orderRepository.findByUserId(userId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping
    public ResponseEntity<List<OrderEntity>> getAllOrders() {
        List<OrderEntity> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }
    
    public static class CreateOrderRequest {
        private String userId;
        private String productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        
        // Getters and setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    }
}