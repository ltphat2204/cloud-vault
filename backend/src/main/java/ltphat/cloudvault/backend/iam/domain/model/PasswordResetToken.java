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
public class PasswordResetToken {
    private String token;
    private String userEmail;
    private LocalDateTime expiryDate;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
