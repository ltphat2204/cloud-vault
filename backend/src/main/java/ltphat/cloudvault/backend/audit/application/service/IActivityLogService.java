package ltphat.cloudvault.backend.audit.application.service;

import ltphat.cloudvault.backend.audit.application.dto.ActivityLogDto;
import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface IActivityLogService {
    void logActivity(UUID userId, ActivityAction action, ResourceType resourceType, UUID resourceId, Map<String, Object> details);
    
    Page<ActivityLogDto> getUserActivityLogs(UUID userId, ActivityAction action, ResourceType resourceType, Pageable pageable);
    
    Page<ActivityLogDto> getResourceActivityLogs(UUID resourceId, ResourceType resourceType, UUID userId, Pageable pageable);
}
