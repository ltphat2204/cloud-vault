package ltphat.cloudvault.backend.audit.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ActivityLog;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import ltphat.cloudvault.backend.audit.domain.repository.IActivityLogRepository;
import ltphat.cloudvault.backend.audit.infrastructure.persistence.jpa.JpaActivityLog;
import ltphat.cloudvault.backend.audit.infrastructure.persistence.jpa.SpringDataActivityLogRepository;
import ltphat.cloudvault.backend.audit.infrastructure.persistence.mapper.ActivityLogPersistenceMapper;
import ltphat.cloudvault.backend.shared.dto.CursorParams;
import ltphat.cloudvault.backend.shared.utils.CursorUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
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
    public List<ActivityLog> findByUserId(UUID userId, ActivityAction action, ResourceType resourceType, CursorParams cursorParams) {
        LocalDateTime cursorTime = null;
        UUID cursorId = null;
        String[] decoded = CursorUtils.decode(cursorParams.getCursor());
        if (decoded != null) {
            cursorTime = LocalDateTime.parse(decoded[0]);
            cursorId = UUID.fromString(decoded[1]);
        }

        Pageable pageable = PageRequest.of(0, cursorParams.getPageSize() + 1, 
                Sort.by(Sort.Direction.DESC, "createdAt", "id"));

        return springRepository.findByFiltersWithCursor(userId, action, resourceType, cursorTime, cursorId, pageable)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ActivityLog> findByResourceIdAndResourceType(UUID resourceId, ResourceType resourceType, CursorParams cursorParams) {
        LocalDateTime cursorTime = null;
        UUID cursorId = null;
        String[] decoded = CursorUtils.decode(cursorParams.getCursor());
        if (decoded != null) {
            cursorTime = LocalDateTime.parse(decoded[0]);
            cursorId = UUID.fromString(decoded[1]);
        }

        Pageable pageable = PageRequest.of(0, cursorParams.getPageSize() + 1, 
                Sort.by(Sort.Direction.DESC, "createdAt", "id"));

        return springRepository.findByResourceIdAndResourceTypeWithCursor(resourceId, resourceType, cursorTime, cursorId, pageable)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
