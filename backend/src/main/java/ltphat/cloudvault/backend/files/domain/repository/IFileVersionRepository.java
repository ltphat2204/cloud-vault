package ltphat.cloudvault.backend.files.domain.repository;

import ltphat.cloudvault.backend.files.domain.model.FileVersion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFileVersionRepository {
    FileVersion save(FileVersion fileVersion);
    Optional<FileVersion> findById(UUID id);
    List<FileVersion> findByFileId(UUID fileId);
    Optional<FileVersion> findByFileIdAndVersionNumber(UUID fileId, Integer versionNumber);
}
