package ltphat.cloudvault.backend.files.domain.repository;

import ltphat.cloudvault.backend.files.domain.model.File;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFileRepository {
    File save(File file);
    Optional<File> findById(UUID id);
    List<File> findByProjectIdAndFolderId(UUID projectId, UUID folderId);
    boolean existsByNameAndFolderIdAndProjectId(String name, UUID folderId, UUID projectId);
}
