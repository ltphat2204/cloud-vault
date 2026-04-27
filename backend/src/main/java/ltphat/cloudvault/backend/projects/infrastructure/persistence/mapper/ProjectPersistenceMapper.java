package ltphat.cloudvault.backend.projects.infrastructure.persistence.mapper;

import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.infrastructure.persistence.jpa.JpaProject;

public interface ProjectPersistenceMapper {
    Project toDomain(JpaProject jpaProject);
    JpaProject toEntity(Project project);
}
