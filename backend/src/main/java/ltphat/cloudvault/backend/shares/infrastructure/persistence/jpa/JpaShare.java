package ltphat.cloudvault.backend.shares.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;
import ltphat.cloudvault.backend.shares.domain.model.Permission;
import ltphat.cloudvault.backend.shares.domain.model.ResourceType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shares", indexes = {
        @Index(name = "idx_shares_shared_with", columnList = "shared_with_user_id"),
        @Index(name = "idx_shares_resource", columnList = "resource_type, resource_id"),
        @Index(name = "idx_shares_token", columnList = "access_token")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JpaShare {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 20)
    private ResourceType resourceType;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @Column(name = "shared_with_user_id")
    private UUID sharedWithUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Permission permission;

    @Column(name = "access_token", unique = true)
    private UUID accessToken;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
