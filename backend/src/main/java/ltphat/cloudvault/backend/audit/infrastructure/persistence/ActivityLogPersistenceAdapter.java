package ltphat.cloudvault.backend.audit.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ActivityLog;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import ltphat.cloudvault.backend.audit.domain.repository.IActivityLogRepository;
import ltphat.cloudvault.backend.audit.infrastructure.persistence.jpa.JpaActivityLog;
import ltphat.cloudvault.backend.audit.infrastructure.persistence.jpa.SpringDataActivityLogRepository;
import ltphat.cloudvault.backend.audit.infrastructure.persistence.mapper.ActivityLogPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ActivityLogPersistenceAdapter implements IActivityLogRepository {

    private final SpringDataActivityLogRepository springRepository;
    private final ActivityLogPersistenceMapper mapper;

    @Override
    public ActivityLog save(ActivityLog activityLog) {
        JpaActivityLog jpa = mapper.toJpa(activityLog);
        JpaActivityLog saved = springRepository.save(jpa);
        return mapper.toDomain(saved);
    }

    @Override
    public Page<ActivityLog> findByUserId(UUID userId, ActivityAction action, ResourceType resourceType, Pageable pageable) {
        return springRepository.findByFilters(userId, action, resourceType, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<ActivityLog> findByResourceIdAndResourceType(UUID resourceId, ResourceType resourceType, Pageable pageable) {
        return springRepository.findByResourceIdAndResourceType(resourceId, resourceType, pageable)
                .map(mapper::toDomain);
    }
}
