package ltphat.cloudvault.backend.iam.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.iam.domain.model.PasswordResetToken;
import ltphat.cloudvault.backend.iam.domain.repository.IPasswordResetTokenRepository;
import ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa.SpringDataPasswordResetTokenRepository;
import ltphat.cloudvault.backend.iam.infrastructure.persistence.mapper.TokenPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter implements IPasswordResetTokenRepository {

    private final SpringDataPasswordResetTokenRepository repository;

    @Override
    public void save(PasswordResetToken token) {
        repository.save(TokenPersistenceMapper.toEntity(token));
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return repository.findById(token).map(TokenPersistenceMapper::toDomain);
    }

    @Override
    public Optional<PasswordResetToken> findByUserEmail(String userEmail) {
        return repository.findByUserEmail(userEmail).map(TokenPersistenceMapper::toDomain);
    }

    @Override
    public void deleteByToken(String token) {
        repository.deleteById(token);
    }

    @Override
    public void deleteByUserEmail(String userEmail) {
        repository.deleteByUserEmail(userEmail);
    }
}
