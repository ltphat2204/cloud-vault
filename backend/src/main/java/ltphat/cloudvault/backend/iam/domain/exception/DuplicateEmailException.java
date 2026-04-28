package ltphat.cloudvault.backend.iam.domain.exception;

public class DuplicateEmailException extends AuthException {
    private static final long serialVersionUID = 1L;
    public DuplicateEmailException(String email) {
        super("Email already exists: " + email);
    }
}
