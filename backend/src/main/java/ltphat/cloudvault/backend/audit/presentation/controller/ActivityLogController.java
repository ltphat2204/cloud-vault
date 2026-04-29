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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @Operation(summary = "List user activity logs", description = "Fetch all activity logs for the authenticated user")
    public ResponseEntity<ApiResponse<Page<ActivityLogDto>>> listActivities(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) ActivityAction action,
            @RequestParam(required = false) ResourceType resourceType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<ActivityLogDto> result = activityLogService.getUserActivityLogs(userPrincipal.getId(), action, resourceType, pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "Activity logs retrieved successfully"));
    }

    @GetMapping("/resources/{resourceId}")
    @Operation(summary = "Get resource activity history", description = "Fetch the activity history for a specific resource")
    public ResponseEntity<ApiResponse<Page<ActivityLogDto>>> getResourceHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID resourceId,
            @RequestParam ResourceType resourceType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<ActivityLogDto> result = activityLogService.getResourceActivityLogs(resourceId, resourceType, userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "Resource history retrieved successfully"));
    }
}
