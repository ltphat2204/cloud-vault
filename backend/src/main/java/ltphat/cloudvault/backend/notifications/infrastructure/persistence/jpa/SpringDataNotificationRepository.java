package ltphat.cloudvault.backend.notifications.infrastructure.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SpringDataNotificationRepository extends JpaRepository<JpaNotification, UUID> {
    Page<JpaNotification> findAllByUserId(UUID userId, Pageable pageable);
    Page<JpaNotification> findAllByUserIdAndReadFalse(UUID userId, Pageable pageable);

    @Modifying
    @Query("UPDATE JpaNotification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    void markAllAsReadForUser(@Param("userId") UUID userId);
}
