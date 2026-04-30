package ltphat.cloudvault.backend.iam.domain.repository;

import ltphat.cloudvault.backend.iam.domain.model.VerificationToken;

import java.util.Optional;

public interface IVerificationTokenRepository {
    void save(VerificationToken token);
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUserEmail(String userEmail);
    void deleteByToken(String token);
    void deleteByUserEmail(String userEmail);
}
