package ltphat.cloudvault.backend.notifications.application.service.impl;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.notifications.application.dto.NotificationDTO;
import ltphat.cloudvault.backend.notifications.application.service.NotificationService;
import ltphat.cloudvault.backend.notifications.domain.exception.NotificationNotFoundException;
import ltphat.cloudvault.backend.notifications.domain.model.Notification;
import ltphat.cloudvault.backend.notifications.domain.model.NotificationType;
import ltphat.cloudvault.backend.notifications.domain.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Page<NotificationDTO> getUserNotifications(UUID userId, boolean unreadOnly, Pageable pageable) {
        Page<Notification> notifications = unreadOnly
                ? notificationRepository.findAllByUserIdAndReadFalse(userId, pageable)
                : notificationRepository.findAllByUserId(userId, pageable);

        return notifications.map(this::toDTO);
    }

    @Override
    @Transactional
    public void markAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        if (!notification.isRead()) {
            notification = notification.markAsRead();
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    @Override
    @Transactional
    public void createNotification(UUID userId, NotificationType type, String message, Map<String, Object> metadata) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .message(message)
                .read(false)
                .metadata(metadata)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // Broadcast via WebSocket
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                toDTO(notification));
    }

    private NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .read(notification.isRead())
                .metadata(notification.getMetadata())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
