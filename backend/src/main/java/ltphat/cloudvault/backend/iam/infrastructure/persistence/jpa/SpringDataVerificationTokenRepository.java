package ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataVerificationTokenRepository extends JpaRepository<JpaVerificationToken, String> {
    Optional<JpaVerificationToken> findByUserEmail(String userEmail);
    void deleteByUserEmail(String userEmail);
}
