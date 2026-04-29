package ltphat.cloudvault.backend.search.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.iam.domain.repository.IUserRepository;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import ltphat.cloudvault.backend.shared.AbstractIntegrationTest;
import ltphat.cloudvault.backend.shared.security.JwtTokenProvider;
import ltphat.cloudvault.backend.shares.domain.model.Permission;
import ltphat.cloudvault.backend.shares.domain.model.ResourceType;
import ltphat.cloudvault.backend.shares.domain.model.Share;
import ltphat.cloudvault.backend.shares.domain.repository.ShareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class SearchIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private IUserRepository userRepository;
    @Autowired private IProjectRepository projectRepository;
    @Autowired private IFolderRepository folderRepository;
    @Autowired private IFileRepository fileRepository;
    @Autowired private ShareRepository shareRepository;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    private User user;
    private User otherUser;
    private Project project;
    private String userToken;
    private String otherUserToken;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("user@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .name("User")
                .build();
        user = userRepository.save(user);
        userToken = jwtTokenProvider.generateAccessToken(user.getEmail());

        otherUser = User.builder()
                .email("other@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .name("Other User")
                .build();
        otherUser = userRepository.save(otherUser);
        otherUserToken = jwtTokenProvider.generateAccessToken(otherUser.getEmail());

        project = Project.createNew("User Project", user.getId());
        project = projectRepository.save(project);

        // Create some files and folders
        Folder folder = Folder.builder()
                .name("Documents")
                .projectId(project.getId())
                .ownerId(user.getId())
                .build();
        folder = folderRepository.save(folder);

        File file = File.builder()
                .name("report.pdf")
                .projectId(project.getId())
                .folderId(folder.getId())
                .ownerId(user.getId())
                .mimeType("application/pdf")
                .minioKey("dummy-key-1")
                .size(1024L)
                .versionNumber(1)
                .build();
        fileRepository.save(file);

        File anotherFile = File.builder()
                .name("notes.txt")
                .projectId(project.getId())
                .ownerId(user.getId())
                .mimeType("text/plain")
                .minioKey("dummy-key-2")
                .size(512L)
                .versionNumber(1)
                .build();
        fileRepository.save(anotherFile);
    }

    @Test
    void search_Global_FindsAll() throws Exception {
        mockMvc.perform(get("/search")
                        .header("Authorization", "Bearer " + userToken)
                        .param("q", "report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("report.pdf"));
    }

    @Test
    void search_InProject_FindsItems() throws Exception {
        mockMvc.perform(get("/search")
                        .header("Authorization", "Bearer " + userToken)
                        .param("q", "notes")
                        .param("projectId", project.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("notes.txt"));
    }

    @Test
    void search_SharedResource_Visibility() throws Exception {
        // Share project with otherUser
        Share share = Share.createInternal(ResourceType.PROJECT, project.getId(), project.getId(), otherUser.getId(), Permission.VIEW);
        shareRepository.save(share);

        // otherUser searches
        mockMvc.perform(get("/search")
                        .header("Authorization", "Bearer " + otherUserToken)
                        .param("q", "report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("report.pdf"));
    }

    @Test
    void search_AccessDenied_ReturnsError() throws Exception {
        // otherUser searches user's project without share
        mockMvc.perform(get("/search")
                        .header("Authorization", "Bearer " + otherUserToken)
                        .param("q", "report")
                        .param("projectId", project.getId().toString()))
                .andExpect(status().isForbidden());
    }
}
