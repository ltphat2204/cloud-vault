package ltphat.cloudvault.backend.shares.infrastructure.persistence.jpa;

import ltphat.cloudvault.backend.shares.domain.model.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataShareRepository extends JpaRepository<JpaShare, UUID> {
    Optional<JpaShare> findByAccessToken(UUID accessToken);
    List<JpaShare> findByResourceTypeAndResourceId(ResourceType resourceType, UUID resourceId);
    List<JpaShare> findBySharedWithUserIdOrderByCreatedAtDesc(UUID sharedWithUserId);
    boolean existsByResourceTypeAndResourceIdAndSharedWithUserId(ResourceType resourceType, UUID resourceId, UUID sharedWithUserId);
}
