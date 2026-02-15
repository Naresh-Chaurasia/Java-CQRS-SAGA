package com.payment.platform.order.data;

import com.payment.platform.order.model.OrderEntity;
import com.payment.platform.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    
    List<OrderEntity> findByUserId(String userId);
    
    List<OrderEntity> findByOrderStatus(OrderStatus status);
    
    Optional<OrderEntity> findByOrderId(String orderId);
    
    List<OrderEntity> findByUserIdAndOrderStatus(String userId, OrderStatus status);
}