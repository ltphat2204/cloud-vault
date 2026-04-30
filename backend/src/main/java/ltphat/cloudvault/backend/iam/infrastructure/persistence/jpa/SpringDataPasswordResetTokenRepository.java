package ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataPasswordResetTokenRepository extends JpaRepository<JpaPasswordResetToken, String> {
    Optional<JpaPasswordResetToken> findByUserEmail(String userEmail);
    void deleteByUserEmail(String userEmail);
}
