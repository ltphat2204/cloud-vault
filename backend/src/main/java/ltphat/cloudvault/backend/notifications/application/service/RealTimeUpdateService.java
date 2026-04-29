package ltphat.cloudvault.backend.notifications.application.service;

import ltphat.cloudvault.backend.notifications.domain.model.RealTimeUpdateType;

import java.util.Map;
import java.util.UUID;

public interface RealTimeUpdateService {
    /**
     * Sends a real-time synchronization event to a specific user via WebSocket.
     * 
     * @param userId The ID of the recipient user.
     * @param type The type of update event.
     * @param metadata Additional context for the event (resource IDs, names, etc.).
     */
    void sendSyncEvent(UUID userId, RealTimeUpdateType type, Map<String, Object> metadata);
}
