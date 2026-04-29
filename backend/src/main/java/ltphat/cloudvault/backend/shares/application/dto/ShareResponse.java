package ltphat.cloudvault.backend.shares.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ltphat.cloudvault.backend.shares.domain.model.Permission;
import ltphat.cloudvault.backend.shares.domain.model.ResourceType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareResponse {
    private UUID id;
    private ResourceType resourceType;
    private UUID resourceId;
    private UUID projectId;
    private UUID folderId;
    private String resourceName; // Added for convenience in "Shared with me"
    private SharedUserDto sharedWithUser;
    private String sharedBy; // Email of the sharer
    private Permission permission;
    private UUID accessToken;
    private String publicUrl;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SharedUserDto {
        private UUID id;
        private String email;
    }
}
