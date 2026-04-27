package ltphat.cloudvault.backend.iam.domain.exception;

public class InvalidCredentialsException extends AuthException {
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
