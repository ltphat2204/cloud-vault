package ltphat.cloudvault.backend.files.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ltphat.cloudvault.backend.files.application.dto.FileDto;
import ltphat.cloudvault.backend.files.application.dto.UpdateFileRequest;
import ltphat.cloudvault.backend.files.application.service.IFileService;
import ltphat.cloudvault.backend.iam.application.dto.UserDto;
import ltphat.cloudvault.backend.iam.application.service.IAuthService;
import ltphat.cloudvault.backend.iam.infrastructure.security.SecurityConfig;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@AutoConfigureMockMvc
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IFileService fileService;

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
                .authorities(java.util.Collections.emptyList())
                .build();
        
        UserDto user = UserDto.builder().id(userId).email("test@example.com").build();
        when(authService.getMe(anyString())).thenReturn(user);
        
        when(jwtTokenProvider.extractAccessTokenUsername(anyString())).thenReturn("test@example.com");
        when(jwtTokenProvider.isAccessTokenValid(anyString(), anyString())).thenReturn(true);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(principal);
    }

    @Test
    void getFile_ReturnsFile() throws Exception {
        UUID fileId = UUID.randomUUID();
        FileDto response = FileDto.builder().id(fileId).name("test.txt").build();

        when(fileService.getFile(any(), any())).thenReturn(response);

        mockMvc.perform(get("/files/{id}", fileId)
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("test.txt"));
    }

    @Test
    void updateFileMetadata_ReturnsUpdated() throws Exception {
        UUID fileId = UUID.randomUUID();
        UpdateFileRequest request = UpdateFileRequest.builder().name("new.txt").build();
        FileDto response = FileDto.builder().id(fileId).name("new.txt").build();

        when(fileService.updateFileMetadata(any(), any(), any())).thenReturn(response);

        mockMvc.perform(patch("/files/{id}", fileId)
                        .header("Authorization", "Bearer dummy-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("new.txt"));
    }
}
