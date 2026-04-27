package ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataUserRepository extends JpaRepository<JpaUser, UUID> {
    Optional<JpaUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
