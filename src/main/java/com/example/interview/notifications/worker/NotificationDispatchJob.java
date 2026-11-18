package com.example.interview.notifications.worker;

import com.example.interview.notifications.delivery.DeliveryClient;
import com.example.interview.notifications.domain.DeliveryStatus;
import com.example.interview.notifications.domain.Notification;
import com.example.interview.notifications.persistence.NotificationRepository;
import java.time.Clock;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "notifications.worker.enabled", havingValue = "true", matchIfMissing = true)
public class NotificationDispatchJob {
    private final NotificationRepository repository;
    private final DeliveryClient deliveryClient;
    private final Clock clock;

    public NotificationDispatchJob(NotificationRepository repository, DeliveryClient deliveryClient, Clock clock) {
        this.repository = repository;
        this.deliveryClient = deliveryClient;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${notifications.poll-delay-ms:10000}")
    public void scheduledDispatch() {
        dispatchBatch();
    }

    public int dispatchBatch() {
        List<Notification> due = repository
                .findTop10ByStatusAndNextAttemptAtLessThanEqualOrderByIdAsc(
                        DeliveryStatus.PENDING, clock.instant());

        for (Notification notification : due) {
            try {
                deliveryClient.send(
                        notification.getNotificationKey(),
                        notification.getRecipient(),
                        notification.getMessageBody()
                );
                notification.markSent();
            } catch (RuntimeException ex) {
                notification.recordFailure(clock.instant());
            }
            repository.save(notification);
        }
        return due.size();
    }
}
