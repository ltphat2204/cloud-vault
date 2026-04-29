package ltphat.cloudvault.backend.shared.exception;

import ltphat.cloudvault.backend.iam.domain.exception.AuthException;
import ltphat.cloudvault.backend.iam.domain.exception.TokenSecurityException;
import ltphat.cloudvault.backend.projects.domain.exception.ProjectException;
import ltphat.cloudvault.backend.projects.domain.exception.ProjectNotFoundException;
import ltphat.cloudvault.backend.shares.domain.exception.ShareException;
import ltphat.cloudvault.backend.shares.domain.exception.ShareNotFoundException;
import ltphat.cloudvault.backend.files.domain.exception.FileException;
import ltphat.cloudvault.backend.files.domain.exception.FileNotFoundException;
import ltphat.cloudvault.backend.folders.domain.exception.FolderException;
import ltphat.cloudvault.backend.folders.domain.exception.FolderNotFoundException;
import ltphat.cloudvault.backend.notifications.domain.exception.NotificationException;
import ltphat.cloudvault.backend.notifications.domain.exception.NotificationNotFoundException;
import ltphat.cloudvault.backend.shared.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<ApiResponse<Void>> handleProjectException(ProjectException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (e instanceof ProjectNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(ShareException.class)
    public ResponseEntity<ApiResponse<Void>> handleShareException(ShareException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (e instanceof ShareNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileException(FileException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (e instanceof FileNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(FolderException.class)
    public ResponseEntity<ApiResponse<Void>> handleFolderException(FolderException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (e instanceof FolderNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotificationException(NotificationException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (e instanceof NotificationNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }
        return ResponseEntity.status(status)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Resource not found: " + e.getResourcePath()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("File size exceeds the maximum limit of 100MB."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
    }
}
