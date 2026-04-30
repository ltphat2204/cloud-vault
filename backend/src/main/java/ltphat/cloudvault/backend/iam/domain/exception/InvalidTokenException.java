package ltphat.cloudvault.backend.iam.domain.exception;

public class InvalidTokenException extends AuthException {
    public InvalidTokenException() {
        super("Invalid token");
    }
}
