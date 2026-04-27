package ltphat.cloudvault.backend.iam.domain.exception;

public class DuplicateEmailException extends AuthException {
    public DuplicateEmailException(String email) {
        super("Email already exists: " + email);
    }
}
