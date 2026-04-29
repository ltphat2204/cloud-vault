package ltphat.cloudvault.backend.audit.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDto {
    private UUID id;
    private UUID userId;
    private ActivityAction action;
    private ResourceType resourceType;
    private UUID resourceId;
    private Map<String, Object> details;
    private LocalDateTime createdAt;
}
