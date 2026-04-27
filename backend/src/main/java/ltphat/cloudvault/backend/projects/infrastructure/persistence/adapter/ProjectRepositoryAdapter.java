package ltphat.cloudvault.backend.projects.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import ltphat.cloudvault.backend.projects.infrastructure.persistence.jpa.JpaProject;
import ltphat.cloudvault.backend.projects.infrastructure.persistence.jpa.SpringDataProjectRepository;
import ltphat.cloudvault.backend.projects.infrastructure.persistence.mapper.ProjectPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements IProjectRepository {

    private final SpringDataProjectRepository springDataProjectRepository;
    private final ProjectPersistenceMapper projectPersistenceMapper;

    @Override
    public Project save(Project project) {
        JpaProject jpaProject = projectPersistenceMapper.toEntity(project);
        JpaProject savedJpaProject = springDataProjectRepository.save(jpaProject);
        return projectPersistenceMapper.toDomain(savedJpaProject);
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return springDataProjectRepository.findById(id)
                .map(projectPersistenceMapper::toDomain);
    }

    @Override
    public List<Project> findByOwnerId(UUID ownerId) {
        return springDataProjectRepository.findByOwnerIdAndDeletedAtIsNull(ownerId).stream()
                .map(projectPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springDataProjectRepository.deleteById(id);
    }
}
