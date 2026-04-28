package ltphat.cloudvault.backend.folders.domain.exception;

import java.util.UUID;

public class FolderNotFoundException extends RuntimeException {
    public FolderNotFoundException(UUID id) {
        super("Folder not found with id: " + id);
    }
}
