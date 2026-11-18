package com.example.interview.notifications.delivery;

/**
 * Simulated provider boundary. An actual provider's idempotency semantics are
 * unknown; callers must not assume that supplying a key guarantees deduplication.
 */
public interface DeliveryClient {
    void send(String notificationKey, String recipient, String messageBody);
}
