package ltphat.cloudvault.backend.files.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataFileRepository extends JpaRepository<JpaFile, UUID> {
    List<JpaFile> findByProjectIdAndFolderId(UUID projectId, UUID folderId);
    boolean existsByNameAndFolderIdAndProjectId(String name, UUID folderId, UUID projectId);
}
