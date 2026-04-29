package ltphat.cloudvault.backend.audit.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ActivityLog {
    private UUID id;
    private UUID userId;
    private ActivityAction action;
    private ResourceType resourceType;
    private UUID resourceId;
    private Map<String, Object> details;
    private LocalDateTime createdAt;
}
