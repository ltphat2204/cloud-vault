package ltphat.cloudvault.backend.folders.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ltphat.cloudvault.backend.folders.application.dto.CreateFolderRequest;
import ltphat.cloudvault.backend.folders.application.dto.FolderDto;
import ltphat.cloudvault.backend.folders.application.dto.UpdateFolderRequest;
import ltphat.cloudvault.backend.folders.application.service.IFolderService;
import ltphat.cloudvault.backend.iam.application.dto.UserDto;
import ltphat.cloudvault.backend.iam.application.service.IAuthService;
import ltphat.cloudvault.backend.shared.dto.CursorPageResponse;
import ltphat.cloudvault.backend.shared.security.JwtAuthenticationFilter;
import ltphat.cloudvault.backend.shared.security.JwtTokenProvider;
import ltphat.cloudvault.backend.iam.infrastructure.security.SecurityConfig;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FolderController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@AutoConfigureMockMvc
class FolderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IFolderService folderService;

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
    void createFolder_ReturnsCreated() throws Exception {
        CreateFolderRequest request = new CreateFolderRequest("New Folder", UUID.randomUUID(), null);
        FolderDto response = FolderDto.builder().id(UUID.randomUUID()).name("New Folder").build();

        when(folderService.createFolder(any(), any())).thenReturn(response);

        mockMvc.perform(post("/folders")
                        .header("Authorization", "Bearer dummy-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("New Folder"));
    }

    @Test
    void listFolders_ReturnsList() throws Exception {
        UUID projectId = UUID.randomUUID();
        FolderDto folder = FolderDto.builder().id(UUID.randomUUID()).name("Folder 1").build();
        CursorPageResponse<FolderDto> response = CursorPageResponse.<FolderDto>builder()
                .items(Collections.singletonList(folder))
                .hasNext(false)
                .build();

        when(folderService.listFolders(any(), any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/folders")
                        .header("Authorization", "Bearer dummy-token")
                        .param("projectId", projectId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].name").value("Folder 1"));
    }

    @Test
    void getFolder_ReturnsFolder() throws Exception {
        UUID folderId = UUID.randomUUID();
        FolderDto response = FolderDto.builder().id(folderId).name("Test Folder").build();

        when(folderService.getFolder(any(), any())).thenReturn(response);

        mockMvc.perform(get("/folders/{id}", folderId)
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Test Folder"));
    }

    @Test
    void updateFolder_ReturnsUpdated() throws Exception {
        UUID folderId = UUID.randomUUID();
        UpdateFolderRequest request = new UpdateFolderRequest("Updated Name");
        FolderDto response = FolderDto.builder().id(folderId).name("Updated Name").build();

        when(folderService.updateFolder(any(), any(), any())).thenReturn(response);

        mockMvc.perform(patch("/folders/{id}", folderId)
                        .header("Authorization", "Bearer dummy-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Name"));
    }

    @Test
    void deleteFolder_ReturnsNoContent() throws Exception {
        UUID folderId = UUID.randomUUID();

        mockMvc.perform(delete("/folders/{id}", folderId)
                        .header("Authorization", "Bearer dummy-token")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Folder deleted successfully"));
    }

    @Test
    void downloadFolder_ReturnsZip() throws Exception {
        UUID folderId = UUID.randomUUID();
        FolderDto folder = FolderDto.builder().id(folderId).name("TestFolder").build();

        when(folderService.getFolder(any(), any())).thenReturn(folder);

        mockMvc.perform(get("/folders/{id}/download", folderId)
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"TestFolder.zip\""))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Content-Type", "application/zip"));
    }
}
