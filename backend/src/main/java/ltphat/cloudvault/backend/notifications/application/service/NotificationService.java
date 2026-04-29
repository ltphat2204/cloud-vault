package ltphat.cloudvault.backend.notifications.application.service;

import ltphat.cloudvault.backend.notifications.application.dto.NotificationDTO;
import ltphat.cloudvault.backend.notifications.domain.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface NotificationService {
    Page<NotificationDTO> getUserNotifications(UUID userId, boolean unreadOnly, Pageable pageable);
    void markAsRead(UUID userId, UUID notificationId);
    void markAllAsRead(UUID userId);
    
    // For internal use by other modules
    void createNotification(UUID userId, NotificationType type, String message, Map<String, Object> metadata);
}
