package ltphat.cloudvault.backend.files.domain.exception;

import java.util.UUID;

public class FileNotFoundException extends FileException {
    private static final long serialVersionUID = 1L;
    public FileNotFoundException(UUID id) {
        super("File not found with ID: " + id);
    }
}
