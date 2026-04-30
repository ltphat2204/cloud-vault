package ltphat.cloudvault.backend.audit.integration;

import ltphat.cloudvault.backend.files.application.service.IFileService;
import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.iam.domain.repository.IUserRepository;
import ltphat.cloudvault.backend.projects.application.dto.ProjectDto;
import ltphat.cloudvault.backend.projects.application.service.IProjectService;
import ltphat.cloudvault.backend.projects.application.dto.CreateProjectRequest;
import ltphat.cloudvault.backend.shared.AbstractIntegrationTest;
import ltphat.cloudvault.backend.shared.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class ActivityLogIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private IUserRepository userRepository;
    @Autowired private IFileService fileService;
    @Autowired private IProjectService projectService;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    private User user;
    private String token;
    private ProjectDto project;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("audit@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .name("Audit User")
                .build();
        user = userRepository.save(user);
        token = jwtTokenProvider.generateAccessToken(user.getEmail());

        project = projectService.createProject(
                CreateProjectRequest.builder().name("Audit Project").build(), 
                user.getId()
        );
    }

    @Test
    void activityLoggingLifecycle() throws Exception {
        // 1. Trigger an activity (Upload File)
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes());
        
        fileService.uploadFile(
                project.getId(), null, "test.txt", "text/plain", 11, 
                multipartFile.getInputStream(), user.getId());

        // 2. Verify activity log exists via API
        mockMvc.perform(get("/audit")
                        .header("Authorization", "Bearer " + token)
                        .param("resourceType", "FILE")
                        .param("action", "FILE_UPLOADED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].action").value("FILE_UPLOADED"))
                .andExpect(jsonPath("$.data.items[0].resourceType").value("FILE"))
                .andExpect(jsonPath("$.data.items[0].details.name").exists()); // Adjust based on details key

        // 3. Get resource history
        UUID fileId = UUID.fromString(
                mockMvc.perform(get("/audit")
                                .header("Authorization", "Bearer " + token))
                        .andReturn().getResponse().getContentAsString()
                        .split("\"resourceId\":\"")[1].split("\"")[0]
        );

        mockMvc.perform(get("/audit/resources/" + fileId)
                        .header("Authorization", "Bearer " + token)
                        .param("resourceType", "FILE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].resourceId").value(fileId.toString()));
    }

    @Test
    void security_OtherUserResourceHistory() throws Exception {
        // 1. Create another user and a file
        User otherUser = User.builder()
                .email("other_audit@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .name("Other User")
                .build();
        otherUser = userRepository.save(otherUser);
        
        ProjectDto otherProject = projectService.createProject(
                CreateProjectRequest.builder().name("Other Project").build(), 
                otherUser.getId()
        );

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "secret.txt", "text/plain", "Secret".getBytes());
        
        var fileDto = fileService.uploadFile(
                otherProject.getId(), null, "secret.txt", "text/plain", 6, 
                multipartFile.getInputStream(), otherUser.getId());

        // 2. Current user tries to access other user's file history
        mockMvc.perform(get("/audit/resources/" + fileDto.getId())
                        .header("Authorization", "Bearer " + token)
                        .param("resourceType", "FILE"))
                .andExpect(status().isForbidden());
    }
}
