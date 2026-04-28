package ltphat.cloudvault.backend.trash.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
import ltphat.cloudvault.backend.shared.dto.ApiResponse;
import ltphat.cloudvault.backend.trash.application.dto.TrashBatchRequest;
import ltphat.cloudvault.backend.trash.application.dto.TrashItemDto;
import ltphat.cloudvault.backend.trash.application.service.ITrashService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trash")
@RequiredArgsConstructor
@Tag(name = "Trash Management", description = "Endpoints for managing deleted files and folders")
public class TrashController {

    private final ITrashService trashService;

    @GetMapping
    @Operation(summary = "List all items in trash")
    public ResponseEntity<ApiResponse<List<TrashItemDto>>> listTrash(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(trashService.listTrash(principal.getId())));
    }

    @PostMapping("/restore")
    @Operation(summary = "Restore items from trash")
    public ResponseEntity<ApiResponse<Void>> restoreItems(
            @RequestBody TrashBatchRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        trashService.restoreItems(request.getItemIds(), principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Items restored successfully"));
    }

    @DeleteMapping
    @Operation(summary = "Permanently delete items")
    public ResponseEntity<ApiResponse<Void>> deletePermanently(
            @RequestBody TrashBatchRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        trashService.deleteItemsPermanently(request.getItemIds(), principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Items deleted permanently"));
    }

    @DeleteMapping("/empty")
    @Operation(summary = "Empty trash")
    public ResponseEntity<ApiResponse<Void>> emptyTrash(@AuthenticationPrincipal UserPrincipal principal) {
        trashService.emptyTrash(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Trash emptied successfully"));
    }

    @PostMapping("/recover-all")
    @Operation(summary = "Recover all items in trash")
    public ResponseEntity<ApiResponse<Void>> recoverAll(@AuthenticationPrincipal UserPrincipal principal) {
        trashService.recoverAll(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "All items recovered successfully"));
    }
}
