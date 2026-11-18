package com.example.interview.notifications.config;

import com.example.interview.notifications.domain.Notification;
import com.example.interview.notifications.persistence.NotificationRepository;
import java.time.Instant;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("demo")
public class DemoDataConfiguration {
    @Bean
    ApplicationRunner seedNotification(NotificationRepository repository) {
        return args -> {
            if (repository.findByNotificationKey("demo-notification-001").isEmpty()) {
                repository.save(new Notification(
                        "demo-notification-001",
                        "demo@example.invalid",
                        "Welcome to the demo application!",
                        Instant.now()
                ));
            }
        };
    }
}
