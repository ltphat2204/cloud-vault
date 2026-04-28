package ltphat.cloudvault.backend.files.application.mapper;

import ltphat.cloudvault.backend.files.application.dto.FileDto;
import ltphat.cloudvault.backend.files.application.dto.FileVersionDto;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.model.FileVersion;
import org.springframework.stereotype.Component;

@Component
public class FileApplicationMapper {
    public FileDto toDto(File file) {
        if (file == null) return null;

        return FileDto.builder()
                .id(file.getId())
                .name(file.getName())
                .size(file.getSize())
                .mimeType(file.getMimeType())
                .folderId(file.getFolderId())
                .projectId(file.getProjectId())
                .ownerId(file.getOwnerId())
                .versionNumber(file.getVersionNumber())
                .currentVersionId(file.getCurrentVersionId())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }

    public FileVersionDto toDto(FileVersion version) {
        if (version == null) return null;

        return FileVersionDto.builder()
                .id(version.getId())
                .fileId(version.getFileId())
                .versionNumber(version.getVersionNumber())
                .size(version.getSize())
                .createdAt(version.getCreatedAt())
                .build();
    }
}
