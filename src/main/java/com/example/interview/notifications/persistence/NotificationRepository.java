package com.example.interview.notifications.persistence;

import com.example.interview.notifications.domain.DeliveryStatus;
import com.example.interview.notifications.domain.Notification;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop10ByStatusAndNextAttemptAtLessThanEqualOrderByIdAsc(
            DeliveryStatus status, Instant now
    );

    Optional<Notification> findByNotificationKey(String notificationKey);
}
