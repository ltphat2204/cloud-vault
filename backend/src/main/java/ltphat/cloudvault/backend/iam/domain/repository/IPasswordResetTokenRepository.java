package ltphat.cloudvault.backend.iam.domain.repository;

import ltphat.cloudvault.backend.iam.domain.model.PasswordResetToken;

import java.util.Optional;

public interface IPasswordResetTokenRepository {
    void save(PasswordResetToken token);
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUserEmail(String userEmail);
    void deleteByToken(String token);
    void deleteByUserEmail(String userEmail);
}
