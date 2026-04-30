package ltphat.cloudvault.backend.audit.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.audit.application.dto.ActivityLogDto;
import ltphat.cloudvault.backend.audit.application.service.IActivityLogService;
import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
import ltphat.cloudvault.backend.shared.dto.ApiResponse;
import ltphat.cloudvault.backend.shared.dto.CursorPageResponse;
import ltphat.cloudvault.backend.shared.dto.CursorParams;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Endpoints for retrieving user activity logs and resource history")
public class ActivityLogController {

    private final IActivityLogService activityLogService;

    @GetMapping
    @Operation(summary = "List user activity logs", description = "Fetch all activity logs for the authenticated user using cursor pagination")
    public ResponseEntity<ApiResponse<CursorPageResponse<ActivityLogDto>>> listActivities(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) ActivityAction action,
            @RequestParam(required = false) ResourceType resourceType,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") Integer size) {
        
        CursorParams params = CursorParams.builder()
                .cursor(cursor)
                .size(size)
                .sortBy("createdAt")
                .direction("DESC")
                .build();

        CursorPageResponse<ActivityLogDto> result = activityLogService.getUserActivityLogs(userPrincipal.getId(), action, resourceType, params);
        return ResponseEntity.ok(ApiResponse.success(result, "Activity logs retrieved successfully"));
    }

    @GetMapping("/resources/{resourceId}")
    @Operation(summary = "Get resource activity history", description = "Fetch the activity history for a specific resource using cursor pagination")
    public ResponseEntity<ApiResponse<CursorPageResponse<ActivityLogDto>>> getResourceHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID resourceId,
            @RequestParam ResourceType resourceType,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") Integer size) {
        
        CursorParams params = CursorParams.builder()
                .cursor(cursor)
                .size(size)
                .sortBy("createdAt")
                .direction("DESC")
                .build();

        CursorPageResponse<ActivityLogDto> result = activityLogService.getResourceActivityLogs(resourceId, resourceType, userPrincipal.getId(), params);
        return ResponseEntity.ok(ApiResponse.success(result, "Resource history retrieved successfully"));
    }
}
