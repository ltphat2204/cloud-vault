package ltphat.cloudvault.backend.shares.domain.model;

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
public class Share {
    private UUID id;
    private ResourceType resourceType;
    private UUID resourceId;
    private UUID sharedWithUserId;
    private Permission permission;
    private UUID accessToken;
    private String passwordHash;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Share createInternal(ResourceType type, UUID resourceId, UUID userId, Permission permission) {
        return Share.builder()
                .resourceType(type)
                .resourceId(resourceId)
                .sharedWithUserId(userId)
                .permission(permission)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Share createPublic(ResourceType type, UUID resourceId, String passwordHash, LocalDateTime expiresAt) {
        return Share.builder()
                .resourceType(type)
                .resourceId(resourceId)
                .accessToken(UUID.randomUUID())
                .passwordHash(passwordHash)
                .expiresAt(expiresAt)
                .permission(Permission.VIEW) // Public links are typically VIEW
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updatePermission(Permission permission) {
        this.permission = permission;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isPublic() {
        return sharedWithUserId == null && accessToken != null;
    }
}
