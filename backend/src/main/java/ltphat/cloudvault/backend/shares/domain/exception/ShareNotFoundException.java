package ltphat.cloudvault.backend.shares.domain.exception;

public class ShareNotFoundException extends ShareException {
    private static final long serialVersionUID = 1L;
    public ShareNotFoundException(String message) {
        super(message);
    }
}
