package ltphat.cloudvault.backend.notifications.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.notifications.application.dto.SyncEventDTO;
import ltphat.cloudvault.backend.notifications.application.service.RealTimeUpdateService;
import ltphat.cloudvault.backend.notifications.domain.model.RealTimeUpdateType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketRealTimeUpdateAdapter implements RealTimeUpdateService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendSyncEvent(UUID userId, RealTimeUpdateType type, Map<String, Object> metadata) {
        SyncEventDTO event = SyncEventDTO.builder()
                .type(type)
                .metadata(metadata)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/sync",
                event
        );
    }

    @Override
    public void sendNotification(UUID userId, Object notification) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );
    }
}
