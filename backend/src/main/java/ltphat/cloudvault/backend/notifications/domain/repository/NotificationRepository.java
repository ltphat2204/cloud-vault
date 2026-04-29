package ltphat.cloudvault.backend.notifications.domain.repository;

import ltphat.cloudvault.backend.notifications.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(UUID id);
    Page<Notification> findAllByUserId(UUID userId, Pageable pageable);
    Page<Notification> findAllByUserIdAndReadFalse(UUID userId, Pageable pageable);
    void markAllAsReadForUser(UUID userId);
}
