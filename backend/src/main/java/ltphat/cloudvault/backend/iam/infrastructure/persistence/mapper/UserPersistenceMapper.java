package ltphat.cloudvault.backend.iam.infrastructure.persistence.mapper;

import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.iam.infrastructure.persistence.jpa.JpaUser;

public interface UserPersistenceMapper {
    User toDomain(JpaUser jpaUser);
    JpaUser toEntity(User user);
}
