package ltphat.cloudvault.backend.files.infrastructure.persistence.mapper;

import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.model.FileVersion;
import ltphat.cloudvault.backend.files.infrastructure.persistence.jpa.JpaFile;
import ltphat.cloudvault.backend.files.infrastructure.persistence.jpa.JpaFileVersion;
import org.springframework.stereotype.Component;

@Component
public class FilePersistenceMapper {

    public JpaFile toEntity(File file) {
        if (file == null) return null;

        return JpaFile.builder()
                .id(file.getId())
                .name(file.getName())
                .minioKey(file.getMinioKey())
                .size(file.getSize())
                .mimeType(file.getMimeType())
                .folderId(file.getFolderId())
                .projectId(file.getProjectId())
                .ownerId(file.getOwnerId())
                .versionNumber(file.getVersionNumber())
                .currentVersionId(file.getCurrentVersionId())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .deletedAt(file.getDeletedAt())
                .build();
    }

    public File toDomain(JpaFile jpaFile) {
        if (jpaFile == null) return null;

        return File.builder()
                .id(jpaFile.getId())
                .name(jpaFile.getName())
                .minioKey(jpaFile.getMinioKey())
                .size(jpaFile.getSize())
                .mimeType(jpaFile.getMimeType())
                .folderId(jpaFile.getFolderId())
                .projectId(jpaFile.getProjectId())
                .ownerId(jpaFile.getOwnerId())
                .versionNumber(jpaFile.getVersionNumber())
                .currentVersionId(jpaFile.getCurrentVersionId())
                .createdAt(jpaFile.getCreatedAt())
                .updatedAt(jpaFile.getUpdatedAt())
                .deletedAt(jpaFile.getDeletedAt())
                .build();
    }

    public JpaFileVersion toEntity(FileVersion version) {
        if (version == null) return null;

        return JpaFileVersion.builder()
                .id(version.getId())
                .fileId(version.getFileId())
                .versionNumber(version.getVersionNumber())
                .minioKey(version.getMinioKey())
                .size(version.getSize())
                .createdAt(version.getCreatedAt())
                .build();
    }

    public FileVersion toDomain(JpaFileVersion jpaVersion) {
        if (jpaVersion == null) return null;

        return FileVersion.builder()
                .id(jpaVersion.getId())
                .fileId(jpaVersion.getFileId())
                .versionNumber(jpaVersion.getVersionNumber())
                .minioKey(jpaVersion.getMinioKey())
                .size(jpaVersion.getSize())
                .createdAt(jpaVersion.getCreatedAt())
                .build();
    }
}
