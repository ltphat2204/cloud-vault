package ltphat.cloudvault.backend.iam.domain.repository;

import ltphat.cloudvault.backend.iam.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    User save(User user);
    boolean existsByEmail(String email);
}
