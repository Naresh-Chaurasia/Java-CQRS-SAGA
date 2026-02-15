package com.payment.platform.notification.data;

import com.payment.platform.notification.model.NotificationEntity;
import com.payment.platform.notification.model.NotificationChannel;
import com.payment.platform.notification.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {
    
    List<NotificationEntity> findByCorrelationId(String correlationId);
    
    List<NotificationEntity> findByEventType(String eventType);
    
    List<NotificationEntity> findByChannel(NotificationChannel channel);
    
    List<NotificationEntity> findByStatus(NotificationStatus status);
    
    Optional<NotificationEntity> findByCorrelationIdAndChannel(String correlationId, NotificationChannel channel);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.status = :status AND n.retryCount < :maxRetries")
    List<NotificationEntity> findFailedNotificationsForRetry(@Param("status") NotificationStatus status, @Param("maxRetries") Integer maxRetries);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.createdAt >= :since")
    List<NotificationEntity> findNotificationsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.status = :status AND n.createdAt >= :since")
    Long countByStatusSince(@Param("status") NotificationStatus status, @Param("since") LocalDateTime since);
}