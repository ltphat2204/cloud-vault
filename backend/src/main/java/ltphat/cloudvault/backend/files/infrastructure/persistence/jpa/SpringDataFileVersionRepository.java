package ltphat.cloudvault.backend.files.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataFileVersionRepository extends JpaRepository<JpaFileVersion, UUID> {
    List<JpaFileVersion> findByFileId(UUID fileId);
    Optional<JpaFileVersion> findByFileIdAndVersionNumber(UUID fileId, Integer versionNumber);
}
