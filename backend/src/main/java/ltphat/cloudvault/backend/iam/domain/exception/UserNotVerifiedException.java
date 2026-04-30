package ltphat.cloudvault.backend.iam.domain.exception;

public class UserNotVerifiedException extends AuthException {
    public UserNotVerifiedException() {
        super("User account is not verified");
    }
}
