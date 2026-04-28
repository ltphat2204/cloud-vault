package ltphat.cloudvault.backend.folders.domain.repository;

import ltphat.cloudvault.backend.folders.domain.model.Folder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFolderRepository {
    Optional<Folder> findById(UUID id);
    Folder save(Folder folder);
    List<Folder> findByProjectIdAndParentFolderId(UUID projectId, UUID parentFolderId);
    boolean existsByNameAndParentFolderIdAndProjectId(String name, UUID parentFolderId, UUID projectId);
    List<Folder> findAllSubfolders(UUID parentFolderId);
    List<Folder> findAllDeletedByOwnerId(UUID ownerId);
    void hardDelete(UUID id);
}
