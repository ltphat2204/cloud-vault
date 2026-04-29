package ltphat.cloudvault.backend.notifications.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class Notification {
    private final UUID id;
    private final UUID userId;
    private final NotificationType type;
    private final String message;
    private final boolean read;
    private final Map<String, Object> metadata;
    private final LocalDateTime createdAt;

    public Notification markAsRead() {
        return Notification.builder()
                .id(this.id)
                .userId(this.userId)
                .type(this.type)
                .message(this.message)
                .read(true)
                .metadata(this.metadata)
                .createdAt(this.createdAt)
                .build();
    }
}
