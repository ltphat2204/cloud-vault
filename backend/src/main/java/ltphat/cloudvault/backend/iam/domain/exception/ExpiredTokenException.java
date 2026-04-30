package ltphat.cloudvault.backend.iam.domain.exception;


public class ExpiredTokenException extends AuthException {
    public ExpiredTokenException() {
        super("Token has expired");
    }
}
