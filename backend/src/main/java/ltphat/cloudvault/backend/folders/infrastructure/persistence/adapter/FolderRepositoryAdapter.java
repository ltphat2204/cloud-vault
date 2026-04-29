package ltphat.cloudvault.backend.folders.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.folders.infrastructure.persistence.jpa.JpaFolder;
import ltphat.cloudvault.backend.folders.infrastructure.persistence.jpa.SpringDataFolderRepository;
import ltphat.cloudvault.backend.folders.infrastructure.persistence.mapper.FolderPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FolderRepositoryAdapter implements IFolderRepository {

    private final SpringDataFolderRepository springDataFolderRepository;
    private final FolderPersistenceMapper folderPersistenceMapper;

    @Override
    public Optional<Folder> findById(UUID id) {
        return springDataFolderRepository.findById(id)
                .map(folderPersistenceMapper::toDomain);
    }

    @Override
    public Folder save(Folder folder) {
        JpaFolder entity = folderPersistenceMapper.toEntity(folder);
        JpaFolder savedEntity = springDataFolderRepository.save(entity);
        return folderPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public List<Folder> findByProjectIdAndParentFolderId(UUID projectId, UUID parentFolderId) {
        return springDataFolderRepository.findByProjectIdAndParentFolderId(projectId, parentFolderId).stream()
                .map(folderPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Folder> findByProjectId(UUID projectId) {
        return springDataFolderRepository.findByProjectId(projectId).stream()
                .map(folderPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameAndParentFolderIdAndProjectId(String name, UUID parentFolderId, UUID projectId) {
        return springDataFolderRepository.existsByNameAndParentFolderIdAndProjectId(name, parentFolderId, projectId);
    }

    @Override
    public List<Folder> findAllSubfolders(UUID parentFolderId) {
        return springDataFolderRepository.findAllDescendants(parentFolderId).stream()
                .map(folderPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Folder> findAllDeletedByOwnerId(UUID ownerId) {
        return springDataFolderRepository.findByOwnerIdAndDeletedAtIsNotNull(ownerId).stream()
                .map(folderPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void hardDelete(UUID id) {
        springDataFolderRepository.deleteById(id);
    }

    @Override
    public List<Folder> findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(List<UUID> projectIds, String query) {
        return springDataFolderRepository.findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(projectIds, query).stream()
                .map(folderPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Folder> findByParentFolderIdAndNameContainingIgnoreCaseAndDeletedAtIsNull(UUID parentFolderId, String query) {
        return springDataFolderRepository.findByParentFolderIdAndNameContainingIgnoreCaseAndDeletedAtIsNull(parentFolderId, query).stream()
                .map(folderPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
