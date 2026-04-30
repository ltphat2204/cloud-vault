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
    private boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User createNew(String email, String passwordHash, String name) {
        return User.builder()
                .email(email)
                .passwordHash(passwordHash)
                .name(name)
                .isVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public User markAsVerified() {
        return User.builder()
                .id(id)
                .email(email)
                .passwordHash(passwordHash)
                .name(name)
                .isVerified(true)
                .createdAt(createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public User updatePasswordHash(String newHash) {
        return User.builder()
                .id(id)
                .email(email)
                .passwordHash(newHash)
                .name(name)
                .isVerified(isVerified)
                .createdAt(createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
