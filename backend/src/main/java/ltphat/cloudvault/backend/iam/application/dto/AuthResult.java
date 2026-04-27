package ltphat.cloudvault.backend.iam.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResult {
    private String accessToken;
    private String refreshToken;
    private UserDto user;
}
