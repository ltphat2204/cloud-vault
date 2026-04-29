package ltphat.cloudvault.backend.folders.domain.exception;

import java.util.UUID;

public class FolderNotFoundException extends FolderException {
    private static final long serialVersionUID = 1L;
    public FolderNotFoundException(UUID id) {
        super("Folder not found with id: " + id);
    }
}
