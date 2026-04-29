package ltphat.cloudvault.backend.shares.infrastructure.persistence.mapper;

import ltphat.cloudvault.backend.shares.domain.model.Share;
import ltphat.cloudvault.backend.shares.infrastructure.persistence.jpa.JpaShare;

public interface SharePersistenceMapper {
    Share toDomain(JpaShare jpaShare);
    JpaShare toEntity(Share share);
}
