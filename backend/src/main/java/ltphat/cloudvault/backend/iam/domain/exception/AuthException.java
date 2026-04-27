package ltphat.cloudvault.backend.iam.domain.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
