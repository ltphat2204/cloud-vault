package ltphat.cloudvault.backend.folders.domain.model;

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
public class Folder {
    private UUID id;
    private String name;
    private UUID parentFolderId;
    private UUID projectId;
    private UUID ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static Folder create(String name, UUID parentFolderId, UUID projectId, UUID ownerId) {
        return Folder.builder()
                .name(name)
                .parentFolderId(parentFolderId)
                .projectId(projectId)
                .ownerId(ownerId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void update(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public void move(UUID newParentFolderId) {
        this.parentFolderId = newParentFolderId;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
