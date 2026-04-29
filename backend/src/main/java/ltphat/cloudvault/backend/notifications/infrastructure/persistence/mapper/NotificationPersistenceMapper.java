package ltphat.cloudvault.backend.notifications.infrastructure.persistence.mapper;

import ltphat.cloudvault.backend.notifications.domain.model.Notification;
import ltphat.cloudvault.backend.notifications.infrastructure.persistence.jpa.JpaNotification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationPersistenceMapper {
    JpaNotification toJpa(Notification notification);
    Notification toDomain(JpaNotification jpaNotification);
}
