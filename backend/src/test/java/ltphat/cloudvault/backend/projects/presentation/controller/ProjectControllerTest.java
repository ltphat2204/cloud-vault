package ltphat.cloudvault.backend.projects.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ltphat.cloudvault.backend.iam.application.dto.UserDto;
import ltphat.cloudvault.backend.iam.application.service.IAuthService;
import ltphat.cloudvault.backend.iam.infrastructure.security.SecurityConfig;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
import ltphat.cloudvault.backend.projects.application.dto.CreateProjectRequest;
import ltphat.cloudvault.backend.projects.application.dto.ProjectDto;
import ltphat.cloudvault.backend.projects.application.dto.UpdateProjectRequest;
import ltphat.cloudvault.backend.projects.application.service.IProjectService;
import ltphat.cloudvault.backend.shared.security.JwtAuthenticationFilter;
import ltphat.cloudvault.backend.shared.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IProjectService projectService;

    @MockitoBean
    private IAuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

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
                .authorities(Collections.emptyList())
                .build();

        UserDto user = UserDto.builder().id(userId).email("test@example.com").build();
        when(authService.getMe(anyString())).thenReturn(user);
        
        when(jwtTokenProvider.extractAccessTokenUsername(anyString())).thenReturn("test@example.com");
        when(jwtTokenProvider.isAccessTokenValid(anyString(), anyString())).thenReturn(true);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(principal);
    }

    @Test
    void createProject_Success() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest("Test Project");
        ProjectDto response = ProjectDto.builder().id(UUID.randomUUID()).name("Test Project").build();

        when(projectService.createProject(any(), any())).thenReturn(response);

        mockMvc.perform(post("/projects")
                        .header("Authorization", "Bearer dummy-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Test Project"));
    }

    @Test
    void listProjects_Success() throws Exception {
        ProjectDto project = ProjectDto.builder().id(UUID.randomUUID()).name("Project 1").build();
        List<ProjectDto> response = Collections.singletonList(project);

        when(projectService.listProjects(any())).thenReturn(response);

        mockMvc.perform(get("/projects")
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Project 1"));
    }

    @Test
    void getProject_Success() throws Exception {
        UUID projectId = UUID.randomUUID();
        ProjectDto response = ProjectDto.builder().id(projectId).name("Test Project").build();

        when(projectService.getProject(any(), any())).thenReturn(response);

        mockMvc.perform(get("/projects/{id}", projectId)
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Test Project"));
    }

    @Test
    void updateProject_Success() throws Exception {
        UUID projectId = UUID.randomUUID();
        UpdateProjectRequest request = new UpdateProjectRequest("Updated Name");
        ProjectDto response = ProjectDto.builder().id(projectId).name("Updated Name").build();

        when(projectService.updateProject(any(), any(), any())).thenReturn(response);

        mockMvc.perform(patch("/projects/{id}", projectId)
                        .header("Authorization", "Bearer dummy-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Name"));
    }

    @Test
    void deleteProject_Success() throws Exception {
        UUID projectId = UUID.randomUUID();

        mockMvc.perform(delete("/projects/{id}", projectId)
                        .header("Authorization", "Bearer dummy-token")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project deleted successfully"));
    }
}
