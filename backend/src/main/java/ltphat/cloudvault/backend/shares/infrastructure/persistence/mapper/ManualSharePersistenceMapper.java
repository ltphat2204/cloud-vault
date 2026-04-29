package ltphat.cloudvault.backend.shares.infrastructure.persistence.mapper;

import ltphat.cloudvault.backend.shares.domain.model.Share;
import ltphat.cloudvault.backend.shares.infrastructure.persistence.jpa.JpaShare;
import org.springframework.stereotype.Component;

@Component
public class ManualSharePersistenceMapper implements SharePersistenceMapper {

    @Override
    public Share toDomain(JpaShare jpaShare) {
        if (jpaShare == null) return null;
        return Share.builder()
                .id(jpaShare.getId())
                .resourceType(jpaShare.getResourceType())
                .resourceId(jpaShare.getResourceId())
                .projectId(jpaShare.getProjectId())
                .sharedWithUserId(jpaShare.getSharedWithUserId())
                .permission(jpaShare.getPermission())
                .accessToken(jpaShare.getAccessToken())
                .passwordHash(jpaShare.getPasswordHash())
                .expiresAt(jpaShare.getExpiresAt())
                .createdAt(jpaShare.getCreatedAt())
                .updatedAt(jpaShare.getUpdatedAt())
                .build();
    }

    @Override
    public JpaShare toEntity(Share share) {
        if (share == null) return null;
        return JpaShare.builder()
                .id(share.getId())
                .resourceType(share.getResourceType())
                .resourceId(share.getResourceId())
                .projectId(share.getProjectId())
                .sharedWithUserId(share.getSharedWithUserId())
                .permission(share.getPermission())
                .accessToken(share.getAccessToken())
                .passwordHash(share.getPasswordHash())
                .expiresAt(share.getExpiresAt())
                .createdAt(share.getCreatedAt())
                .updatedAt(share.getUpdatedAt())
                .build();
    }
}
