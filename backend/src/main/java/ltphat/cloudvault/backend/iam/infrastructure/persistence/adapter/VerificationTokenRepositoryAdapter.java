package ltphat.cloudvault.backend.iam.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.iam.domain.model.VerificationToken;
import ltphat.cloudvault.backend.iam.domain.repository.IVerificationTokenRepository;
import ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa.SpringDataVerificationTokenRepository;
import ltphat.cloudvault.backend.iam.infrastructure.persistence.mapper.TokenPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VerificationTokenRepositoryAdapter implements IVerificationTokenRepository {

    private final SpringDataVerificationTokenRepository repository;

    @Override
    public void save(VerificationToken token) {
        repository.save(TokenPersistenceMapper.toEntity(token));
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return repository.findById(token).map(TokenPersistenceMapper::toDomain);
    }

    @Override
    public Optional<VerificationToken> findByUserEmail(String userEmail) {
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
