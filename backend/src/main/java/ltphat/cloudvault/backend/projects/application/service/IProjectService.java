package ltphat.cloudvault.backend.projects.application.service;

import ltphat.cloudvault.backend.projects.application.dto.CreateProjectRequest;
import ltphat.cloudvault.backend.projects.application.dto.ProjectDto;
import ltphat.cloudvault.backend.projects.application.dto.UpdateProjectRequest;

import java.util.List;
import java.util.UUID;

public interface IProjectService {
    ProjectDto createProject(CreateProjectRequest request, UUID ownerId);
    ProjectDto getProject(UUID id, UUID ownerId);
    List<ProjectDto> listProjects(UUID ownerId);
    ProjectDto updateProject(UUID id, UpdateProjectRequest request, UUID ownerId);
    void deleteProject(UUID id, UUID ownerId);
    void restoreProject(UUID id, UUID ownerId);
    void hardDeleteProject(UUID id, UUID ownerId);
}
