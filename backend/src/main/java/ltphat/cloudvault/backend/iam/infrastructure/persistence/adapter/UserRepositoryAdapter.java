package ltphat.cloudvault.backend.iam.infrastructure.persistence.adapter;

import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.iam.domain.repository.IUserRepository;
import ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa.JpaUser;
import ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa.SpringDataUserRepository;
import ltphat.cloudvault.backend.iam.infrastructure.persistence.mapper.ManualUserPersistenceMapper;
import ltphat.cloudvault.backend.iam.infrastructure.persistence.mapper.UserPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements IUserRepository {

    private final SpringDataUserRepository springDataUserRepository;
    private final UserPersistenceMapper userPersistenceMapper = new ManualUserPersistenceMapper();

    public UserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataUserRepository.findById(id)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public User save(User user) {
        JpaUser jpaUser = userPersistenceMapper.toEntity(user);
        JpaUser savedJpaUser = springDataUserRepository.save(jpaUser);
        return userPersistenceMapper.toDomain(savedJpaUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }
}
