package ltphat.cloudvault.backend.files.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {
    private UUID id;
    private String name;
    private String minioKey;
    private Long size;
    private String mimeType;
    private UUID folderId;
    private UUID projectId;
    private UUID ownerId;
    private Integer versionNumber;
    private UUID currentVersionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static File create(String name, String minioKey, Long size, String mimeType, UUID folderId, UUID projectId, UUID ownerId) {
        return File.builder()
                .name(name)
                .minioKey(minioKey)
                .size(size)
                .mimeType(mimeType)
                .folderId(folderId)
                .projectId(projectId)
                .ownerId(ownerId)
                .versionNumber(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateMetadata(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public void move(UUID targetFolderId) {
        this.folderId = targetFolderId;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void restore() {
        this.deletedAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateVersion(UUID versionId, Integer versionNumber, String minioKey, Long size, String mimeType) {
        this.currentVersionId = versionId;
        this.versionNumber = versionNumber;
        this.minioKey = minioKey;
        this.size = size;
        this.mimeType = mimeType;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCurrentVersionId(UUID versionId) {
        this.currentVersionId = versionId;
        this.updatedAt = LocalDateTime.now();
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public void setMinioKey(String minioKey) {
        this.minioKey = minioKey;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
