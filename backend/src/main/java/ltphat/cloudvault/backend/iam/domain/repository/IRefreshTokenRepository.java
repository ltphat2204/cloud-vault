package ltphat.cloudvault.backend.iam.domain.repository;

import ltphat.cloudvault.backend.iam.domain.model.RefreshToken;

import java.util.Optional;

public interface IRefreshTokenRepository {
    void save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteByUserEmail(String userEmail);
    boolean existsByAccessToken(String accessToken);
    Optional<RefreshToken> findByAccessToken(String accessToken);
}
