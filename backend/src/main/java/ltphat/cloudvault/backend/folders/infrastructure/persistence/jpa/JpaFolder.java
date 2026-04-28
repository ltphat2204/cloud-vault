package ltphat.cloudvault.backend.folders.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "folders", indexes = {
        @Index(name = "idx_folders_project_parent", columnList = "projectId, parentFolderId"),
        @Index(name = "idx_folders_owner", columnList = "ownerId")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JpaFolder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private UUID parentFolderId;

    @Column(nullable = false)
    private UUID projectId;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
