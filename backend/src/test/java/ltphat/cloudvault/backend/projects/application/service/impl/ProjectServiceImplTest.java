package ltphat.cloudvault.backend.projects.application.service.impl;

import ltphat.cloudvault.backend.projects.application.dto.CreateProjectRequest;
import ltphat.cloudvault.backend.projects.application.dto.ProjectDto;
import ltphat.cloudvault.backend.projects.application.dto.UpdateProjectRequest;
import ltphat.cloudvault.backend.projects.application.mapper.ProjectApplicationMapper;
import ltphat.cloudvault.backend.projects.domain.exception.ProjectNotFoundException;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private IProjectRepository projectRepository;

    @Mock
    private ProjectApplicationMapper projectApplicationMapper;

    @Mock
    private ltphat.cloudvault.backend.folders.application.service.IFolderService folderService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void createProject_Success() {
        UUID ownerId = UUID.randomUUID();
        CreateProjectRequest request = new CreateProjectRequest("Test Project");
        Project project = Project.createNew("Test Project", ownerId);
        project = Project.builder()
                .id(UUID.randomUUID())
                .name("Test Project")
                .ownerId(ownerId)
                .build();
        ProjectDto projectDto = ProjectDto.builder().name("Test Project").ownerId(ownerId).build();

        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectApplicationMapper.toDto(any(Project.class))).thenReturn(projectDto);
        when(folderService.createFolder(any(), any())).thenReturn(null);

        ProjectDto result = projectService.createProject(request, ownerId);

        assertThat(result.getName()).isEqualTo("Test Project");
        assertThat(result.getOwnerId()).isEqualTo(ownerId);
        verify(projectRepository).save(any(Project.class));
        verify(folderService).createFolder(argThat(req -> req.getName().equals("Test Project")), eq(ownerId));
    }

    @Test
    void getProject_Success() {
        UUID projectId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        Project project = Project.builder().id(projectId).name("Test Project").ownerId(ownerId).build();
        ProjectDto projectDto = ProjectDto.builder().id(projectId).name("Test Project").ownerId(ownerId).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectApplicationMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.getProject(projectId, ownerId);

        assertThat(result.getId()).isEqualTo(projectId);
        assertThat(result.getName()).isEqualTo("Test Project");
    }

    @Test
    void getProject_NotFound() {
        UUID projectId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProject(projectId, ownerId))
                .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void getProject_AccessDenied() {
        UUID projectId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherOwnerId = UUID.randomUUID();
        Project project = Project.builder().id(projectId).name("Test Project").ownerId(otherOwnerId).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.getProject(projectId, ownerId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getProject_Deleted_ReturnsNotFound() {
        UUID projectId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        Project project = Project.builder()
                .id(projectId)
                .name("Deleted Project")
                .ownerId(ownerId)
                .deletedAt(LocalDateTime.now())
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.getProject(projectId, ownerId))
                .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void listProjects_Success() {
        UUID ownerId = UUID.randomUUID();
        Project project = Project.builder().name("Project 1").ownerId(ownerId).build();
        ProjectDto projectDto = ProjectDto.builder().name("Project 1").ownerId(ownerId).build();

        when(projectRepository.findByOwnerId(ownerId)).thenReturn(List.of(project));
        when(projectApplicationMapper.toDto(project)).thenReturn(projectDto);

        List<ProjectDto> result = projectService.listProjects(ownerId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Project 1");
    }

    @Test
    void updateProject_Success() {
        UUID projectId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        Project project = Project.builder().id(projectId).name("Old Name").ownerId(ownerId).build();
        UpdateProjectRequest request = new UpdateProjectRequest("New Name");
        Project updatedProject = Project.builder().id(projectId).name("New Name").ownerId(ownerId).build();
        ProjectDto projectDto = ProjectDto.builder().id(projectId).name("New Name").ownerId(ownerId).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);
        when(projectApplicationMapper.toDto(any(Project.class))).thenReturn(projectDto);

        ProjectDto result = projectService.updateProject(projectId, request, ownerId);

        assertThat(result.getName()).isEqualTo("New Name");
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void deleteProject_Success() {
        UUID projectId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        Project project = Project.builder().id(projectId).name("To Delete").ownerId(ownerId).build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.deleteProject(projectId, ownerId);

        verify(projectRepository).save(argThat(p -> p.getDeletedAt() != null));
    }
}
