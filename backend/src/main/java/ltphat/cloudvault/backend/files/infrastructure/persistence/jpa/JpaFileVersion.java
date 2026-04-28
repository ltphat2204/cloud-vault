package ltphat.cloudvault.backend.files.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_versions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JpaFileVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "file_id", nullable = false)
    private UUID fileId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "minio_key", nullable = false)
    private String minioKey;

    @Column(nullable = false)
    private Long size;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
