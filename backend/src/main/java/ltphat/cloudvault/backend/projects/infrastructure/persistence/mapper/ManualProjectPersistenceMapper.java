package ltphat.cloudvault.backend.projects.infrastructure.persistence.mapper;

import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.infrastructure.persistence.jpa.JpaProject;
import org.springframework.stereotype.Component;

@Component
public class ManualProjectPersistenceMapper implements ProjectPersistenceMapper {

    @Override
    public Project toDomain(JpaProject jpaProject) {
        if (jpaProject == null) return null;
        return Project.builder()
                .id(jpaProject.getId())
                .name(jpaProject.getName())
                .ownerId(jpaProject.getOwnerId())
                .createdAt(jpaProject.getCreatedAt())
                .updatedAt(jpaProject.getUpdatedAt())
                .deletedAt(jpaProject.getDeletedAt())
                .build();
    }

    @Override
    public JpaProject toEntity(Project project) {
        if (project == null) return null;
        return JpaProject.builder()
                .id(project.getId())
                .name(project.getName())
                .ownerId(project.getOwnerId())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .deletedAt(project.getDeletedAt())
                .build();
    }
}
