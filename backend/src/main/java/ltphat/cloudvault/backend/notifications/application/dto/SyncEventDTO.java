package ltphat.cloudvault.backend.notifications.application.dto;

import lombok.Builder;
import lombok.Getter;
import ltphat.cloudvault.backend.notifications.domain.model.RealTimeUpdateType;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class SyncEventDTO {
    private RealTimeUpdateType type;
    private Map<String, Object> metadata;
    private LocalDateTime timestamp;
}
