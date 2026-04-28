package ltphat.cloudvault.backend.folders.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ltphat.cloudvault.backend.iam.application.dto.UserDto;
import ltphat.cloudvault.backend.iam.application.service.IAuthService;
import ltphat.cloudvault.backend.folders.application.dto.CreateFolderRequest;
import ltphat.cloudvault.backend.folders.application.dto.FolderDto;
import ltphat.cloudvault.backend.folders.application.service.IFolderService;
import ltphat.cloudvault.backend.shared.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FolderController.class)
@AutoConfigureMockMvc(addFilters = false)
class FolderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IFolderService folderService;

    @MockitoBean
    private IAuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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
        
        UserDto user = UserDto.builder().id(userId).email("test@example.com").build();
        when(authService.getMe(anyString())).thenReturn(user);
    }

    @Test
    void createFolder_ReturnsCreated() throws Exception {
        CreateFolderRequest request = CreateFolderRequest.builder()
                .name("Test Folder")
                .projectId(UUID.randomUUID())
                .build();

        FolderDto response = FolderDto.builder()
                .id(UUID.randomUUID())
                .name("Test Folder")
                .build();

        when(folderService.createFolder(any(), any())).thenReturn(response);

        mockMvc.perform(post("/folders")
                        .with(user(principal))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Test Folder"));
    }
}
