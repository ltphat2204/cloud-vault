package ltphat.cloudvault.backend.folders.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.iam.application.dto.UserDto;
import ltphat.cloudvault.backend.iam.application.service.IAuthService;
import ltphat.cloudvault.backend.folders.application.dto.CreateFolderRequest;
import ltphat.cloudvault.backend.folders.application.dto.FolderDto;
import ltphat.cloudvault.backend.folders.application.dto.MoveFolderRequest;
import ltphat.cloudvault.backend.folders.application.dto.UpdateFolderRequest;
import ltphat.cloudvault.backend.folders.application.service.IFolderService;
import ltphat.cloudvault.backend.shared.dto.ApiResponse;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
@Tag(name = "Folders", description = "Folder management APIs")
public class FolderController {

    private final IFolderService folderService;

    @PostMapping
    @Operation(summary = "Create folder", description = "Creates a new folder within a project or parent folder")
    public ResponseEntity<ApiResponse<FolderDto>> createFolder(
            @RequestBody CreateFolderRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        FolderDto folder = folderService.createFolder(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(folder, "Folder created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get folder details", description = "Retrieves metadata for a specific folder")
    public ResponseEntity<ApiResponse<FolderDto>> getFolder(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        FolderDto folder = folderService.getFolder(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(folder));
    }

    @GetMapping
    @Operation(summary = "List folders", description = "Lists folders within a project or specific parent folder")
    public ResponseEntity<ApiResponse<List<FolderDto>>> listFolders(
            @RequestParam UUID projectId,
            @RequestParam(required = false) UUID parentFolderId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<FolderDto> folders = folderService.listFolders(projectId, parentFolderId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(folders));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update folder", description = "Renames an existing folder")
    public ResponseEntity<ApiResponse<FolderDto>> updateFolder(
            @PathVariable UUID id,
            @RequestBody UpdateFolderRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        FolderDto folder = folderService.updateFolder(id, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(folder, "Folder updated successfully"));
    }

    @PatchMapping("/{id}/move")
    @Operation(summary = "Move folder", description = "Moves a folder to a new parent location within the same project")
    public ResponseEntity<ApiResponse<FolderDto>> moveFolder(
            @PathVariable UUID id,
            @RequestBody MoveFolderRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        FolderDto folder = folderService.moveFolder(id, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(folder, "Folder moved successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete folder", description = "Soft deletes a folder and its contents")
    public ResponseEntity<ApiResponse<Void>> deleteFolder(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        folderService.deleteFolder(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Folder deleted successfully"));
    }

}
