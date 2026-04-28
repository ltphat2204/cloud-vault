package ltphat.cloudvault.backend.files.domain.exception;

import java.util.UUID;

public class FileNotFoundException extends FileException {
    public FileNotFoundException(UUID id) {
        super("File not found with ID: " + id);
    }
}
