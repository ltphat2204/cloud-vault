package ltphat.cloudvault.backend.projects.application.mapper;

import ltphat.cloudvault.backend.projects.application.dto.ProjectDto;
import ltphat.cloudvault.backend.projects.domain.model.Project;

public interface ProjectApplicationMapper {
    ProjectDto toDto(Project project);
}
