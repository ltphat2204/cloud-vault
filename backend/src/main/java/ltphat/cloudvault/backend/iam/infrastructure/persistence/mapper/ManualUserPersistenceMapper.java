package ltphat.cloudvault.backend.iam.infrastructure.persistence.mapper;

import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa.JpaUser;

public class ManualUserPersistenceMapper implements UserPersistenceMapper {

    @Override
    public User toDomain(JpaUser jpaUser) {
        if (jpaUser == null) return null;
        return User.builder()
                .id(jpaUser.getId())
                .email(jpaUser.getEmail())
                .passwordHash(jpaUser.getPasswordHash())
                .name(jpaUser.getName())
                .isVerified(jpaUser.isVerified())
                .createdAt(jpaUser.getCreatedAt())
                .updatedAt(jpaUser.getUpdatedAt())
                .build();
    }

    @Override
    public JpaUser toEntity(User user) {
        if (user == null) return null;
        return JpaUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .name(user.getName())
                .isVerified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
