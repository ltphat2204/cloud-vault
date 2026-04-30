package ltphat.cloudvault.backend.audit.domain.repository;

import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ActivityLog;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import ltphat.cloudvault.backend.shared.dto.CursorParams;

import java.util.List;
import java.util.UUID;

public interface IActivityLogRepository {
    ActivityLog save(ActivityLog activityLog);
    
    List<ActivityLog> findByUserId(UUID userId, ActivityAction action, ResourceType resourceType, CursorParams cursorParams);
    
    List<ActivityLog> findByResourceIdAndResourceType(UUID resourceId, ResourceType resourceType, CursorParams cursorParams);
}
