package ltphat.cloudvault.backend.files.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.files.infrastructure.persistence.jpa.JpaFile;
import ltphat.cloudvault.backend.files.infrastructure.persistence.jpa.SpringDataFileRepository;
import ltphat.cloudvault.backend.files.infrastructure.persistence.mapper.FilePersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileRepositoryAdapter implements IFileRepository {

    private final SpringDataFileRepository springDataFileRepository;
    private final FilePersistenceMapper filePersistenceMapper;

    @Override
    public File save(File file) {
        JpaFile jpaFile = filePersistenceMapper.toEntity(file);
        JpaFile savedFile = springDataFileRepository.save(jpaFile);
        return filePersistenceMapper.toDomain(savedFile);
    }

    @Override
    public Optional<File> findById(UUID id) {
        return springDataFileRepository.findById(id)
                .map(filePersistenceMapper::toDomain);
    }

    @Override
    public List<File> findByProjectIdAndFolderId(UUID projectId, UUID folderId) {
        return springDataFileRepository.findByProjectIdAndFolderId(projectId, folderId).stream()
                .map(filePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameAndFolderIdAndProjectId(String name, UUID folderId, UUID projectId) {
        return springDataFileRepository.existsByNameAndFolderIdAndProjectId(name, folderId, projectId);
    }

    @Override
    public List<File> findAllDeletedByOwnerId(UUID ownerId) {
        return springDataFileRepository.findByOwnerIdAndDeletedAtIsNotNull(ownerId).stream()
                .map(filePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void hardDelete(UUID id) {
        springDataFileRepository.deleteById(id);
    }

    @Override
    public List<File> findByFolderId(UUID folderId) {
        return springDataFileRepository.findByFolderId(folderId).stream()
                .map(filePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<File> findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(List<UUID> projectIds, String query) {
        return springDataFileRepository.findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(projectIds, query).stream()
                .map(filePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<File> findByFolderIdAndNameContainingIgnoreCaseAndDeletedAtIsNull(UUID folderId, String query) {
        return springDataFileRepository.findByFolderIdAndNameContainingIgnoreCaseAndDeletedAtIsNull(folderId, query).stream()
                .map(filePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
