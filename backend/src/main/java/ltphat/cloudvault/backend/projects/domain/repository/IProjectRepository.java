package ltphat.cloudvault.backend.projects.domain.repository;

import ltphat.cloudvault.backend.projects.domain.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProjectRepository {
    Project save(Project project);
    Optional<Project> findById(UUID id);
    List<Project> findByOwnerId(UUID ownerId);
    List<Project> findAllDeletedByOwnerId(UUID ownerId);
    void deleteById(UUID id);
}
