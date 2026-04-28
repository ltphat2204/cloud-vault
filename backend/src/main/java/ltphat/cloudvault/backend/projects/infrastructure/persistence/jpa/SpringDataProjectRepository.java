package ltphat.cloudvault.backend.projects.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataProjectRepository extends JpaRepository<JpaProject, UUID> {
    List<JpaProject> findByOwnerIdAndDeletedAtIsNull(UUID ownerId);
    List<JpaProject> findByOwnerIdAndDeletedAtIsNotNull(UUID ownerId);
}
