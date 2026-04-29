package ltphat.cloudvault.backend.shares.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ltphat.cloudvault.backend.shares.domain.model.Permission;
import ltphat.cloudvault.backend.shares.domain.model.ResourceType;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareResourceRequest {
    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;

    @NotNull(message = "Resource ID is required")
    private UUID resourceId;

    @Email(message = "Invalid email format")
    @NotNull(message = "User email is required")
    private String userEmail;

    @NotNull(message = "Permission is required")
    private Permission permission;
}
