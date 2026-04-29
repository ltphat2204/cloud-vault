package ltphat.cloudvault.backend.shares.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.iam.domain.repository.IUserRepository;
import ltphat.cloudvault.backend.shared.security.JwtTokenProvider;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import ltphat.cloudvault.backend.shared.AbstractIntegrationTest;
import ltphat.cloudvault.backend.shares.application.dto.ShareResourceRequest;
import ltphat.cloudvault.backend.shares.domain.model.Permission;
import ltphat.cloudvault.backend.shares.domain.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class ShareIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private IUserRepository userRepository;
    @Autowired private IProjectRepository projectRepository;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    private User owner;
    private User recipient;
    private Project project;
    private String ownerToken;
    private String recipientToken;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .email("owner@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .name("Owner User")
                .build();
        owner = userRepository.save(owner);
        ownerToken = jwtTokenProvider.generateAccessToken(owner.getEmail());

        recipient = User.builder()
                .email("recipient@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .name("Recipient User")
                .build();
        recipient = userRepository.save(recipient);
        recipientToken = jwtTokenProvider.generateAccessToken(recipient.getEmail());

        project = Project.createNew("Test Project", owner.getId());
        project = projectRepository.save(project);
    }

    @Test
    void shareProjectAndListSharedWithMe() throws Exception {
        ShareResourceRequest request = ShareResourceRequest.builder()
                .resourceType(ResourceType.PROJECT)
                .resourceId(project.getId())
                .userEmail(recipient.getEmail())
                .permission(Permission.VIEW)
                .build();

        // 1. Owner shares with recipient
        mockMvc.perform(post("/shares")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // 2. Recipient lists "Shared with me"
        mockMvc.perform(get("/shares/shared-with-me")
                        .header("Authorization", "Bearer " + recipientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].resourceName").value("Test Project"))
                .andExpect(jsonPath("$.data[0].sharedBy").value("owner@example.com"));
    }

    @Test
    void publicLinkAccess() throws Exception {
        // 1. Owner creates public link
        String request = """
                {
                    "resourceType": "PROJECT",
                    "resourceId": "%s",
                    "password": "link-password"
                }
                """.formatted(project.getId());

        String response = mockMvc.perform(post("/shares/public")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("data").get("accessToken").asText();

        // 2. Access with correct password
        mockMvc.perform(get("/shares/public/" + token)
                        .param("password", "link-password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.resourceName").value("Test Project"));

        // 3. Access with wrong password
        mockMvc.perform(get("/shares/public/" + token)
                        .param("password", "wrong"))
                .andExpect(status().isBadRequest()); // ShareException mapped to 400
    }
}
