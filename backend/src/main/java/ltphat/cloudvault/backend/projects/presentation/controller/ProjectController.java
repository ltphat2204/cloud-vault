package ltphat.cloudvault.backend.projects.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.projects.application.dto.CreateProjectRequest;
import ltphat.cloudvault.backend.projects.application.dto.ProjectDto;
import ltphat.cloudvault.backend.projects.application.dto.UpdateProjectRequest;
import ltphat.cloudvault.backend.projects.application.service.IProjectService;
import ltphat.cloudvault.backend.shared.dto.ApiResponse;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management APIs")
public class ProjectController {

    private final IProjectService projectService;

    @PostMapping
    @Operation(summary = "Create project", description = "Creates a new project for the authenticated user")
    public ResponseEntity<ApiResponse<ProjectDto>> createProject(
            @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProjectDto project = projectService.createProject(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(project, "Project created successfully"));
    }

    @GetMapping
    @Operation(summary = "List projects", description = "Retrieves all projects owned by the authenticated user")
    public ResponseEntity<ApiResponse<List<ProjectDto>>> listProjects(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<ProjectDto> projects = projectService.listProjects(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(projects));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project details", description = "Retrieves metadata for a specific project")
    public ResponseEntity<ApiResponse<ProjectDto>> getProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProjectDto project = projectService.getProject(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(project));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update project", description = "Updates metadata (e.g., name) for an existing project")
    public ResponseEntity<ApiResponse<ProjectDto>> updateProject(
            @PathVariable UUID id,
            @RequestBody UpdateProjectRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProjectDto project = projectService.updateProject(id, request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(project, "Project updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project", description = "Permanently deletes a project and its associated resources")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        projectService.deleteProject(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Project deleted successfully"));
    }

}
