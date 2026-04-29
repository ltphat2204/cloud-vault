package ltphat.cloudvault.backend.shares.domain.exception;

public class ShareException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ShareException(String message) {
        super(message);
    }
}
