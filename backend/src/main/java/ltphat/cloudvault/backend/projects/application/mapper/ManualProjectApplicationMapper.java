package ltphat.cloudvault.backend.projects.application.mapper;

import ltphat.cloudvault.backend.projects.application.dto.ProjectDto;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ManualProjectApplicationMapper implements ProjectApplicationMapper {

    @Override
    public ProjectDto toDto(Project project) {
        if (project == null) return null;
        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .ownerId(project.getOwnerId())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .deletedAt(project.getDeletedAt())
                .build();
    }
}
