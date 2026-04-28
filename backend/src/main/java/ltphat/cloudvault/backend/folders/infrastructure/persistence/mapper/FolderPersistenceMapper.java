package ltphat.cloudvault.backend.folders.infrastructure.persistence.mapper;

import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.infrastructure.persistence.jpa.JpaFolder;
import org.springframework.stereotype.Component;

@Component
public class FolderPersistenceMapper {
    public JpaFolder toEntity(Folder folder) {
        if (folder == null) return null;
        
        return JpaFolder.builder()
                .id(folder.getId())
                .name(folder.getName())
                .parentFolderId(folder.getParentFolderId())
                .projectId(folder.getProjectId())
                .ownerId(folder.getOwnerId())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .deletedAt(folder.getDeletedAt())
                .build();
    }

    public Folder toDomain(JpaFolder entity) {
        if (entity == null) return null;

        return Folder.builder()
                .id(entity.getId())
                .name(entity.getName())
                .parentFolderId(entity.getParentFolderId())
                .projectId(entity.getProjectId())
                .ownerId(entity.getOwnerId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
}
