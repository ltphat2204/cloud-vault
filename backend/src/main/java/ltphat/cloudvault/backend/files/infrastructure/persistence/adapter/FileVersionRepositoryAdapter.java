package ltphat.cloudvault.backend.files.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.files.domain.model.FileVersion;
import ltphat.cloudvault.backend.files.domain.repository.IFileVersionRepository;
import ltphat.cloudvault.backend.files.infrastructure.persistence.jpa.JpaFileVersion;
import ltphat.cloudvault.backend.files.infrastructure.persistence.jpa.SpringDataFileVersionRepository;
import ltphat.cloudvault.backend.files.infrastructure.persistence.mapper.FilePersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileVersionRepositoryAdapter implements IFileVersionRepository {

    private final SpringDataFileVersionRepository springDataFileVersionRepository;
    private final FilePersistenceMapper filePersistenceMapper;

    @Override
    public FileVersion save(FileVersion fileVersion) {
        JpaFileVersion entity = filePersistenceMapper.toEntity(fileVersion);
        JpaFileVersion saved = springDataFileVersionRepository.save(entity);
        return filePersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<FileVersion> findById(UUID id) {
        return springDataFileVersionRepository.findById(id)
                .map(filePersistenceMapper::toDomain);
    }

    @Override
    public List<FileVersion> findByFileId(UUID fileId) {
        return springDataFileVersionRepository.findByFileId(fileId).stream()
                .map(filePersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FileVersion> findByFileIdAndVersionNumber(UUID fileId, Integer versionNumber) {
        return springDataFileVersionRepository.findByFileIdAndVersionNumber(fileId, versionNumber)
                .map(filePersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        springDataFileVersionRepository.deleteById(id);
    }
}
