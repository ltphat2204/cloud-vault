package ltphat.cloudvault.backend.audit.application.mapper;

import ltphat.cloudvault.backend.audit.application.dto.ActivityLogDto;
import ltphat.cloudvault.backend.audit.domain.model.ActivityLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityLogApplicationMapper {
    ActivityLogDto toDto(ActivityLog activityLog);
}
