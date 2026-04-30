package ltphat.cloudvault.backend.audit.application.service;

import ltphat.cloudvault.backend.audit.application.dto.ActivityLogDto;
import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import ltphat.cloudvault.backend.shared.dto.CursorPageResponse;
import ltphat.cloudvault.backend.shared.dto.CursorParams;

import java.util.Map;
import java.util.UUID;

public interface IActivityLogService {
    void logActivity(UUID userId, ActivityAction action, ResourceType resourceType, UUID resourceId, Map<String, Object> details);
    
    CursorPageResponse<ActivityLogDto> getUserActivityLogs(UUID userId, ActivityAction action, ResourceType resourceType, CursorParams cursorParams);
    
    CursorPageResponse<ActivityLogDto> getResourceActivityLogs(UUID resourceId, ResourceType resourceType, UUID userId, CursorParams cursorParams);
}
