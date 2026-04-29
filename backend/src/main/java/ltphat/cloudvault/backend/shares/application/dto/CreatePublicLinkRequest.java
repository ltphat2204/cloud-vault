package ltphat.cloudvault.backend.shares.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ltphat.cloudvault.backend.shares.domain.model.ResourceType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePublicLinkRequest {
    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;

    @NotNull(message = "Resource ID is required")
    private UUID resourceId;

    private String password;

    private LocalDateTime expiresAt;
}
