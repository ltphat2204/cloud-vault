package ltphat.cloudvault.backend.shared.exception;

import ltphat.cloudvault.backend.iam.domain.exception.AuthException;
import ltphat.cloudvault.backend.iam.domain.exception.TokenSecurityException;
import ltphat.cloudvault.backend.shared.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthException e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        if (e instanceof TokenSecurityException) {
            status = HttpStatus.FORBIDDEN;
        }
        return ResponseEntity.status(status)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
    }
}
