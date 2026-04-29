package ltphat.cloudvault.backend.shares.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
import ltphat.cloudvault.backend.shared.dto.ApiResponse;
import ltphat.cloudvault.backend.shares.application.dto.*;
import ltphat.cloudvault.backend.shares.application.service.ShareService;
import ltphat.cloudvault.backend.shares.domain.model.ResourceType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shares")
@RequiredArgsConstructor
@Tag(name = "Shares", description = "Resource sharing management APIs")
public class ShareController {

    private final ShareService shareService;

    @PostMapping
    @Operation(summary = "Share resource", description = "Shares a project, folder, or file with another user")
    public ResponseEntity<ApiResponse<ShareResponse>> shareResource(
            @Valid @RequestBody ShareResourceRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ShareResponse response = shareService.shareResource(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Resource shared successfully"));
    }

    @PostMapping("/public")
    @Operation(summary = "Create public link", description = "Generates a public access link for a resource")
    public ResponseEntity<ApiResponse<ShareResponse>> createPublicLink(
            @Valid @RequestBody CreatePublicLinkRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ShareResponse response = shareService.createPublicLink(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Public link created successfully"));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update share permission", description = "Updates the access level for an existing share")
    public ResponseEntity<ApiResponse<Void>> updateShare(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateShareRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shareService.updateShare(id, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Share permission updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Revoke share", description = "Removes access for a shared user or deletes a public link")
    public ResponseEntity<ApiResponse<Void>> revokeShare(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        shareService.revokeShare(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Share revoked successfully"));
    }

    @GetMapping("/resource/{type}/{id}")
    @Operation(summary = "List shares for resource", description = "Retrieves all active shares for a specific resource")
    public ResponseEntity<ApiResponse<List<ShareResponse>>> getSharesForResource(
            @PathVariable ResourceType type,
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<ShareResponse> response = shareService.getSharesForResource(type, id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/shared-with-me")
    @Operation(summary = "List shared with me", description = "Retrieves all resources shared with the authenticated user")
    public ResponseEntity<ApiResponse<List<ShareResponse>>> getResourcesSharedWithMe(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<ShareResponse> response = shareService.getResourcesSharedWithMe(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/public/{token}")
    @Operation(summary = "Access public link", description = "Retrieves resource metadata using a public token")
    public ResponseEntity<ApiResponse<ShareResponse>> getPublicShare(
            @PathVariable UUID token,
            @RequestParam(required = false) String password
    ) {
        ShareResponse response = shareService.getPublicShare(token, password);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
