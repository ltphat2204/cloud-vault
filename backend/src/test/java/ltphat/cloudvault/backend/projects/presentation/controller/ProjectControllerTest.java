package ltphat.cloudvault.backend.projects.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ltphat.cloudvault.backend.iam.application.dto.UserDto;
import ltphat.cloudvault.backend.iam.application.service.IAuthService;
import ltphat.cloudvault.backend.projects.application.dto.CreateProjectRequest;
import ltphat.cloudvault.backend.projects.application.dto.ProjectDto;
import ltphat.cloudvault.backend.projects.application.dto.UpdateProjectRequest;
import ltphat.cloudvault.backend.projects.application.service.IProjectService;
import ltphat.cloudvault.backend.shared.security.JwtAuthenticationFilter;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IProjectService projectService;

    @MockitoBean
    private IAuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter; // Needed because it's a required bean in SecurityConfig

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        principal = UserPrincipal.builder()
                .id(userId)
                .email("test@example.com")
                .authorities(java.util.Collections.emptyList())
                .build();
        
        when(authService.getMe("test@example.com")).thenReturn(UserDto.builder().id(userId).build());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createProject_Success() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest("New Project");
        ProjectDto projectDto = ProjectDto.builder().id(UUID.randomUUID()).name("New Project").build();

        when(projectService.createProject(any(CreateProjectRequest.class), eq(userId))).thenReturn(projectDto);

        mockMvc.perform(post("/projects")
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("New Project"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void listProjects_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        ProjectDto projectDto = ProjectDto.builder().name("Project 1").build();

        when(authService.getMe("test@example.com")).thenReturn(UserDto.builder().id(userId).build());
        when(projectService.listProjects(userId)).thenReturn(List.of(projectDto));

        mockMvc.perform(get("/projects")
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Project 1"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getProject_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        ProjectDto projectDto = ProjectDto.builder().id(projectId).name("Project 1").build();

        when(authService.getMe("test@example.com")).thenReturn(UserDto.builder().id(userId).build());
        when(projectService.getProject(projectId, userId)).thenReturn(projectDto);

        mockMvc.perform(get("/projects/" + projectId)
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(projectId.toString()));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void updateProject_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UpdateProjectRequest request = new UpdateProjectRequest("Updated Name");
        ProjectDto projectDto = ProjectDto.builder().id(projectId).name("Updated Name").build();

        when(authService.getMe("test@example.com")).thenReturn(UserDto.builder().id(userId).build());
        when(projectService.updateProject(eq(projectId), any(UpdateProjectRequest.class), eq(userId)))
                .thenReturn(projectDto);

        mockMvc.perform(patch("/projects/" + projectId)
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Updated Name"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void deleteProject_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(authService.getMe("test@example.com")).thenReturn(UserDto.builder().id(userId).build());

        mockMvc.perform(delete("/projects/" + projectId)
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
