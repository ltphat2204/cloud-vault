package ltphat.cloudvault.backend.shares.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.shares.domain.model.ResourceType;
import ltphat.cloudvault.backend.shares.domain.model.Share;
import ltphat.cloudvault.backend.shares.domain.repository.ShareRepository;
import ltphat.cloudvault.backend.shares.infrastructure.persistence.jpa.JpaShare;
import ltphat.cloudvault.backend.shares.infrastructure.persistence.jpa.SpringDataShareRepository;
import ltphat.cloudvault.backend.shares.infrastructure.persistence.mapper.SharePersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SharePersistenceAdapter implements ShareRepository {

    private final SpringDataShareRepository repository;
    private final SharePersistenceMapper mapper;

    @Override
    public Share save(Share share) {
        JpaShare jpaShare = mapper.toEntity(share);
        JpaShare saved = repository.save(jpaShare);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Share> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Share> findByAccessToken(UUID token) {
        return repository.findByAccessToken(token).map(mapper::toDomain);
    }

    @Override
    public List<Share> findByResource(ResourceType type, UUID id) {
        return repository.findByResourceTypeAndResourceId(type, id).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Share> findBySharedWithUserId(UUID userId) {
        return repository.findBySharedWithUserIdOrderByCreatedAtDesc(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByResourceAndUser(ResourceType type, UUID resourceId, UUID userId) {
        return repository.existsByResourceTypeAndResourceIdAndSharedWithUserId(type, resourceId, userId);
    }
}
