package ltphat.cloudvault.backend.notifications.application.service.impl;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.notifications.application.dto.SyncEventDTO;
import ltphat.cloudvault.backend.notifications.application.service.RealTimeUpdateService;
import ltphat.cloudvault.backend.notifications.domain.model.RealTimeUpdateType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RealTimeUpdateServiceImpl implements RealTimeUpdateService {

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
}
