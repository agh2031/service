package com.example.interview.notifications;

import com.example.interview.notifications.domain.DeliveryStatus;
import com.example.interview.notifications.domain.Notification;
import com.example.interview.notifications.persistence.NotificationRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "notifications.worker.enabled=false")
class NotificationPersistenceIT {
    @Autowired NotificationRepository repository;

    @AfterEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    void findsDuePendingNotifications() {
        Instant now = Instant.parse("2026-01-15T12:00:00Z");
        repository.saveAndFlush(new Notification(
                "due-1", "first@example.invalid", "Hello", now.minusSeconds(60)));
        repository.saveAndFlush(new Notification(
                "later-1", "second@example.invalid", "Later", now.plusSeconds(60)));

        List<Notification> due = repository
                .findTop10ByStatusAndNextAttemptAtLessThanEqualOrderByIdAsc(
                        DeliveryStatus.PENDING, now);

        assertEquals(1, due.size());
        assertEquals("due-1", due.getFirst().getNotificationKey());
        assertTrue(repository.findByNotificationKey("later-1").isPresent());
    }
}
