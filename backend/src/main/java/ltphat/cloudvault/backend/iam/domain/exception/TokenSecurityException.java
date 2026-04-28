package ltphat.cloudvault.backend.iam.domain.exception;

public class TokenSecurityException extends AuthException {
    private static final long serialVersionUID = 1L;
    public TokenSecurityException(String message) {
        super(message);
    }
}
