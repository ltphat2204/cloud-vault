package ltphat.cloudvault.backend.trash.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.trash.application.dto.TrashBatchRequest;
import ltphat.cloudvault.backend.trash.application.dto.TrashItemDto;
import ltphat.cloudvault.backend.trash.application.service.ITrashService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trash")
@RequiredArgsConstructor
@Tag(name = "Trash Management", description = "Endpoints for managing deleted files and folders")
public class TrashController {

    private final ITrashService trashService;

    @GetMapping
    @Operation(summary = "List all items in trash")
    public ResponseEntity<List<TrashItemDto>> listTrash(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(trashService.listTrash(user.getId()));
    }

    @PostMapping("/restore")
    @Operation(summary = "Restore items from trash")
    public ResponseEntity<Void> restoreItems(
            @RequestBody TrashBatchRequest request,
            @AuthenticationPrincipal User user) {
        trashService.restoreItems(request.getItemIds(), user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Operation(summary = "Permanently delete items")
    public ResponseEntity<Void> deletePermanently(
            @RequestBody TrashBatchRequest request,
            @AuthenticationPrincipal User user) {
        trashService.deleteItemsPermanently(request.getItemIds(), user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/empty")
    @Operation(summary = "Empty trash")
    public ResponseEntity<Void> emptyTrash(@AuthenticationPrincipal User user) {
        trashService.emptyTrash(user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/recover-all")
    @Operation(summary = "Recover all items in trash")
    public ResponseEntity<Void> recoverAll(@AuthenticationPrincipal User user) {
        trashService.recoverAll(user.getId());
        return ResponseEntity.ok().build();
    }
}
