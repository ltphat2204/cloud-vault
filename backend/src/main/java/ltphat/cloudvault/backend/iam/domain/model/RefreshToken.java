package ltphat.cloudvault.backend.iam.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private String token;
    private String accessToken; // Linked Access Token
    private String userEmail;
    private String deviceId;
    private String ipAddress;
    private LocalDateTime expiryDate;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
