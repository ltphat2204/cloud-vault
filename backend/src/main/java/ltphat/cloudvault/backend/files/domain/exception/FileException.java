package ltphat.cloudvault.backend.files.domain.exception;

public class FileException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public FileException(String message) {
        super(message);
    }
}
