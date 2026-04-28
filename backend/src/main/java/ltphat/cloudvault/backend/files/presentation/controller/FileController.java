package ltphat.cloudvault.backend.files.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.files.application.dto.FileDto;
import ltphat.cloudvault.backend.files.application.dto.MoveFileRequest;
import ltphat.cloudvault.backend.files.application.dto.UpdateFileRequest;
import ltphat.cloudvault.backend.files.application.dto.FileVersionDto;
import ltphat.cloudvault.backend.files.application.service.IFileService;
import ltphat.cloudvault.backend.shared.dto.ApiResponse;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "File management APIs")
public class FileController {

    private final IFileService fileService;

    @GetMapping("/{id}")
    @Operation(summary = "Get file details", description = "Retrieves metadata and current version info for a specific file")
    public ResponseEntity<ApiResponse<FileDto>> getFile(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        FileDto file = fileService.getFile(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(file));
    }

    @GetMapping
    @Operation(summary = "List files", description = "Lists files within a project or folder")
    public ResponseEntity<ApiResponse<List<FileDto>>> listFiles(
            @RequestParam UUID projectId,
            @RequestParam(required = false) UUID folderId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<FileDto> files = fileService.listFiles(projectId, folderId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(files));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file", description = "Uploads a new file or a new version of an existing file")
    public ResponseEntity<ApiResponse<FileDto>> uploadFile(
            @RequestParam UUID projectId,
            @RequestParam(required = false) UUID folderId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws IOException {
        FileDto savedFile = fileService.uploadFile(
                projectId,
                folderId,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getInputStream(),
                principal.getId()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedFile, "File uploaded successfully"));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download current version", description = "Downloads the latest version of the specified file")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        FileDto fileDto = fileService.getFile(id, principal.getId());
        java.io.InputStream inputStream = fileService.downloadFile(id, null, principal.getId());
        
        Resource resource = new InputStreamResource(inputStream);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileDto.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.getName() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/versions")
    @Operation(summary = "Get version history", description = "Retrieves all versions of a specific file")
    public ResponseEntity<ApiResponse<List<FileVersionDto>>> getVersions(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<FileVersionDto> versions = fileService.getFileVersions(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(versions));
    }

    @GetMapping("/{id}/versions/{versionNumber}/download")
    @Operation(summary = "Download specific version", description = "Downloads a specific version of the file")
    public ResponseEntity<Resource> downloadVersion(
            @PathVariable UUID id,
            @PathVariable Integer versionNumber,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        FileDto fileDto = fileService.getFile(id, principal.getId());
        java.io.InputStream inputStream = fileService.downloadFile(id, versionNumber, principal.getId());
        
        Resource resource = new InputStreamResource(inputStream);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileDto.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.getName() + "\"")
                .body(resource);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update file metadata", description = "Updates metadata (e.g., name) for an existing file")
    public ResponseEntity<ApiResponse<FileDto>> updateFileMetadata(
            @PathVariable UUID id,
            @RequestBody UpdateFileRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        FileDto file = fileService.updateFileMetadata(id, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(file, "File metadata updated successfully"));
    }

    @PutMapping("/{id}/move")
    @Operation(summary = "Move file", description = "Moves a file to a new folder location")
    public ResponseEntity<ApiResponse<FileDto>> moveFile(
            @PathVariable UUID id,
            @RequestBody MoveFileRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        FileDto file = fileService.moveFile(id, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(file, "File moved successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete file", description = "Soft deletes a file")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        fileService.deleteFile(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "File deleted successfully"));
    }

}
