package ltphat.cloudvault.backend.iam.domain.model;

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
public class User {
    private UUID id;
    private String email;
    private String passwordHash;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User createNew(String email, String passwordHash, String name) {
        return User.builder()
                .email(email)
                .passwordHash(passwordHash)
                .name(name)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
