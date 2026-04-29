package ltphat.cloudvault.backend.audit.infrastructure.persistence.mapper;

import ltphat.cloudvault.backend.audit.domain.model.ActivityLog;
import ltphat.cloudvault.backend.audit.infrastructure.persistence.jpa.JpaActivityLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityLogPersistenceMapper {
    JpaActivityLog toJpa(ActivityLog activityLog);
    ActivityLog toDomain(JpaActivityLog jpaActivityLog);
}
