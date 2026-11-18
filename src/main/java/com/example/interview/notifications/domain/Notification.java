package com.example.interview.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_key", nullable = false, unique = true, length = 128)
    private String notificationKey;

    @Column(nullable = false, length = 320)
    private String recipient;

    @Column(name = "message_body", nullable = false, length = 4000)
    private String messageBody;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryStatus status;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    protected Notification() {}

    public Notification(String notificationKey, String recipient, String messageBody, Instant nextAttemptAt) {
        this.notificationKey = notificationKey;
        this.recipient = recipient;
        this.messageBody = messageBody;
        this.nextAttemptAt = nextAttemptAt;
        this.status = DeliveryStatus.PENDING;
        this.attemptCount = 0;
    }

    public Long getId() { return id; }
    public String getNotificationKey() { return notificationKey; }
    public String getRecipient() { return recipient; }
    public String getMessageBody() { return messageBody; }
    public DeliveryStatus getStatus() { return status; }
    public int getAttemptCount() { return attemptCount; }
    public Instant getNextAttemptAt() { return nextAttemptAt; }
    public Instant getCreatedAt() { return createdAt; }

    public void markSent() {
        this.status = DeliveryStatus.SENT;
    }

    public void recordFailure(Instant now) {
        this.attemptCount++;
        this.nextAttemptAt = now;
    }
}
