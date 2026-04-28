package ltphat.cloudvault.backend.folders.application.mapper;

import ltphat.cloudvault.backend.folders.application.dto.FolderDto;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import org.springframework.stereotype.Component;

@Component
public class FolderApplicationMapper {
    public FolderDto toDto(Folder folder) {
        if (folder == null) return null;
        
        return FolderDto.builder()
                .id(folder.getId())
                .name(folder.getName())
                .parentFolderId(folder.getParentFolderId())
                .projectId(folder.getProjectId())
                .ownerId(folder.getOwnerId())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .build();
    }
}
