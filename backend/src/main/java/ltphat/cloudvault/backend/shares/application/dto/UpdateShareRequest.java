package ltphat.cloudvault.backend.shares.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ltphat.cloudvault.backend.shares.domain.model.Permission;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShareRequest {
    @NotNull(message = "Permission is required")
    private Permission permission;
}
