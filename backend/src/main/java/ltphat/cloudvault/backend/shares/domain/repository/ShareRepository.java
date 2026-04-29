package ltphat.cloudvault.backend.shares.domain.repository;

import ltphat.cloudvault.backend.shares.domain.model.ResourceType;
import ltphat.cloudvault.backend.shares.domain.model.Share;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShareRepository {
    Share save(Share share);
    Optional<Share> findById(UUID id);
    Optional<Share> findByAccessToken(UUID token);
    List<Share> findByResource(ResourceType type, UUID id);
    List<Share> findBySharedWithUserId(UUID userId);
    void delete(UUID id);
    boolean existsByResourceAndUser(ResourceType type, UUID resourceId, UUID userId);
}
