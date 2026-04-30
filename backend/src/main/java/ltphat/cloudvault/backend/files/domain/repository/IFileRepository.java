package ltphat.cloudvault.backend.files.domain.repository;

import ltphat.cloudvault.backend.files.domain.model.File;

import ltphat.cloudvault.backend.shared.dto.CursorParams;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFileRepository {
    File save(File file);
    Optional<File> findById(UUID id);
    List<File> findByProjectIdAndFolderId(UUID projectId, UUID folderId, CursorParams cursorParams);
    Optional<File> findByNameAndFolderIdAndProjectId(String name, UUID folderId, UUID projectId);
    boolean existsByNameAndFolderIdAndProjectId(String name, UUID folderId, UUID projectId);
    List<File> findAllDeletedByOwnerId(UUID ownerId);
    void hardDelete(UUID id);
    List<File> findByFolderId(UUID folderId);
    List<File> findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(List<UUID> projectIds, String query);
    List<File> findByFolderIdAndNameContainingIgnoreCaseAndDeletedAtIsNull(UUID folderId, String query);
}
