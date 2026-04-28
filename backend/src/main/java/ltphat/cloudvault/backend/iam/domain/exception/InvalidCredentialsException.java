package ltphat.cloudvault.backend.iam.domain.exception;

public class InvalidCredentialsException extends AuthException {
    private static final long serialVersionUID = 1L;
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
