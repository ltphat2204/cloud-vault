package ltphat.cloudvault.backend.audit.domain.repository;

import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ActivityLog;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IActivityLogRepository {
    ActivityLog save(ActivityLog activityLog);
    
    Page<ActivityLog> findByUserId(UUID userId, ActivityAction action, ResourceType resourceType, Pageable pageable);
    
    Page<ActivityLog> findByResourceIdAndResourceType(UUID resourceId, ResourceType resourceType, Pageable pageable);
}
