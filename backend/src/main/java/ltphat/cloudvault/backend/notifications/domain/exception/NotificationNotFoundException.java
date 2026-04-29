package ltphat.cloudvault.backend.notifications.domain.exception;

import java.util.UUID;

public class NotificationNotFoundException extends NotificationException {
    public NotificationNotFoundException(UUID id) {
        super("Notification not found with ID: " + id);
    }
}
