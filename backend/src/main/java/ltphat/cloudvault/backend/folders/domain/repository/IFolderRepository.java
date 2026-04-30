package ltphat.cloudvault.backend.folders.domain.repository;

import ltphat.cloudvault.backend.folders.domain.model.Folder;

import ltphat.cloudvault.backend.shared.dto.CursorParams;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFolderRepository {
    Optional<Folder> findById(UUID id);
    Folder save(Folder folder);
    List<Folder> findByProjectIdAndParentFolderId(UUID projectId, UUID parentFolderId, CursorParams cursorParams);
    Optional<Folder> findByNameAndParentFolderIdAndProjectId(String name, UUID parentFolderId, UUID projectId);
    List<Folder> findByProjectId(UUID projectId);
    boolean existsByNameAndParentFolderIdAndProjectId(String name, UUID parentFolderId, UUID projectId);
    List<Folder> findAllSubfolders(UUID parentFolderId);
    List<Folder> findAllDeletedByOwnerId(UUID ownerId);
    void hardDelete(UUID id);
    List<Folder> findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(List<UUID> projectIds, String query);
    List<Folder> findByParentFolderIdAndNameContainingIgnoreCaseAndDeletedAtIsNull(UUID parentFolderId, String query);
}
