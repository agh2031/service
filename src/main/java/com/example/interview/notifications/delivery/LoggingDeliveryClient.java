package com.example.interview.notifications.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingDeliveryClient implements DeliveryClient {
    private static final Logger log = LoggerFactory.getLogger(LoggingDeliveryClient.class);

    @Override
    public void send(String notificationKey, String recipient, String messageBody) {
        // Simulates a successful provider request without sending any messages.
        log.info("Simulated delivery: notificationKey={}, recipient={}, bytes={}",
                notificationKey, recipient, messageBody.length());
    }
}
