package ltphat.cloudvault.backend.notifications.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.notifications.domain.model.Notification;
import ltphat.cloudvault.backend.notifications.domain.repository.NotificationRepository;
import ltphat.cloudvault.backend.notifications.infrastructure.persistence.jpa.JpaNotification;
import ltphat.cloudvault.backend.notifications.infrastructure.persistence.jpa.SpringDataNotificationRepository;
import ltphat.cloudvault.backend.notifications.infrastructure.persistence.mapper.NotificationPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationPersistenceAdapter implements NotificationRepository {

    private final SpringDataNotificationRepository springDataRepository;
    private final NotificationPersistenceMapper mapper;

    @Override
    public Notification save(Notification notification) {
        JpaNotification jpa = mapper.toJpa(notification);
        JpaNotification saved = springDataRepository.save(jpa);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Notification> findAllByUserId(UUID userId, Pageable pageable) {
        return springDataRepository.findAllByUserId(userId, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Notification> findAllByUserIdAndReadFalse(UUID userId, Pageable pageable) {
        return springDataRepository.findAllByUserIdAndReadFalse(userId, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void markAllAsReadForUser(UUID userId) {
        springDataRepository.markAllAsReadForUser(userId);
    }
}
