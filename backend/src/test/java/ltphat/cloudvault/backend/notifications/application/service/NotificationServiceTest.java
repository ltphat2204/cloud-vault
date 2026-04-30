package ltphat.cloudvault.backend.notifications.application.service;

import ltphat.cloudvault.backend.notifications.application.dto.NotificationDTO;
import ltphat.cloudvault.backend.notifications.application.service.impl.NotificationServiceImpl;
import ltphat.cloudvault.backend.notifications.domain.exception.NotificationNotFoundException;
import ltphat.cloudvault.backend.notifications.domain.model.Notification;
import ltphat.cloudvault.backend.notifications.domain.model.NotificationType;
import ltphat.cloudvault.backend.notifications.domain.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    
    @Mock
    private RealTimeUpdateService realTimeUpdateService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID userId;
    private UUID notificationId;
    private Notification notification;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        notificationId = UUID.randomUUID();
        notification = Notification.builder()
                .id(notificationId)
                .userId(userId)
                .type(NotificationType.SHARE_RECEIVED)
                .message("Test Message")
                .read(false)
                .metadata(Map.of("key", "value"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getUserNotifications_All_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> page = new PageImpl<>(List.of(notification));
        when(notificationRepository.findAllByUserId(userId, pageable)).thenReturn(page);

        Page<NotificationDTO> result = notificationService.getUserNotifications(userId, false, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getMessage()).isEqualTo("Test Message");
        verify(notificationRepository).findAllByUserId(userId, pageable);
    }

    @Test
    void getUserNotifications_UnreadOnly_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> page = new PageImpl<>(List.of(notification));
        when(notificationRepository.findAllByUserIdAndReadFalse(userId, pageable)).thenReturn(page);

        Page<NotificationDTO> result = notificationService.getUserNotifications(userId, true, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(notificationRepository).findAllByUserIdAndReadFalse(userId, pageable);
    }

    @Test
    void markAsRead_Success() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationService.markAsRead(userId, notificationId);

        verify(notificationRepository).save(argThat(Notification::isRead));
    }

    @Test
    void markAsRead_Forbidden_ThrowsException() {
        UUID otherUserId = UUID.randomUUID();
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.markAsRead(otherUserId, notificationId))
                .isInstanceOf(NotificationNotFoundException.class);
    }

    @Test
    void markAllAsRead_Success() {
        notificationService.markAllAsRead(userId);
        verify(notificationRepository).markAllAsReadForUser(userId);
    }

    @Test
    void createNotification_Success() {
        notificationService.createNotification(userId, NotificationType.SHARE_RECEIVED, "Message", Map.of());
        
        verify(notificationRepository).save(any(Notification.class));
        verify(realTimeUpdateService).sendNotification(eq(userId), any(NotificationDTO.class));
    }
}
