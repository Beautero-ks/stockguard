package dev.bops.stockguard.notification.domain;

import java.time.Instant;
import java.util.UUID;

public record Notification(
        UUID id,
        NotificationChannel channel,
        String recipient,
        String subject,
        String body,
        NotificationStatus status,
        Instant createdAt
) {
    public enum NotificationChannel { EMAIL, SMS, IN_APP }
    public enum NotificationStatus { PENDING, SENT, FAILED }
}