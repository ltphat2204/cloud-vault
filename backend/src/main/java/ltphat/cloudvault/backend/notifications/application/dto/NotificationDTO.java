package ltphat.cloudvault.backend.notifications.application.dto;

import lombok.Builder;
import lombok.Getter;
import ltphat.cloudvault.backend.notifications.domain.model.NotificationType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class NotificationDTO {
    private UUID id;
    private NotificationType type;
    private String message;
    private boolean read;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}
