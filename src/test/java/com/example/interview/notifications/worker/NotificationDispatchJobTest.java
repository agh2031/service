package com.example.interview.notifications.worker;

import com.example.interview.notifications.delivery.DeliveryClient;
import com.example.interview.notifications.domain.DeliveryStatus;
import com.example.interview.notifications.domain.Notification;
import com.example.interview.notifications.persistence.NotificationRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDispatchJobTest {
    private static final Instant NOW = Instant.parse("2026-01-15T12:00:00Z");

    @Mock NotificationRepository repository;
    @Mock DeliveryClient client;

    private NotificationDispatchJob job;

    @BeforeEach
    void setUp() {
        job = new NotificationDispatchJob(
                repository, client, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void successfulSendMarksNotificationAsSent() {
        Notification notice = new Notification(
                "notification-1", "user@example.invalid", "Hello", NOW.minusSeconds(30));
        when(repository.findTop10ByStatusAndNextAttemptAtLessThanEqualOrderByIdAsc(
                eq(DeliveryStatus.PENDING), eq(NOW))).thenReturn(List.of(notice));

        assertEquals(1, job.dispatchBatch());

        verify(client).send("notification-1", "user@example.invalid", "Hello");
        assertEquals(DeliveryStatus.SENT, notice.getStatus());
        verify(repository).save(notice);
    }

    @Test
    void failedSendRemainsPendingAndIncrementsAttemptCount() {
        Notification notice = new Notification(
                "notification-2", "user@example.invalid", "Hello", NOW.minusSeconds(30));
        when(repository.findTop10ByStatusAndNextAttemptAtLessThanEqualOrderByIdAsc(
                eq(DeliveryStatus.PENDING), eq(NOW))).thenReturn(List.of(notice));
        doThrow(new RuntimeException("simulated provider timeout"))
                .when(client).send("notification-2", "user@example.invalid", "Hello");

        assertEquals(1, job.dispatchBatch());

        assertEquals(DeliveryStatus.PENDING, notice.getStatus());
        assertEquals(1, notice.getAttemptCount());
        assertEquals(NOW, notice.getNextAttemptAt());
        verify(repository).save(notice);
    }

    @Test
    void emptyQueueDoesNotCallProvider() {
        when(repository.findTop10ByStatusAndNextAttemptAtLessThanEqualOrderByIdAsc(
                eq(DeliveryStatus.PENDING), eq(NOW))).thenReturn(List.of());

        assertEquals(0, job.dispatchBatch());
        verifyNoInteractions(client);
    }
}
