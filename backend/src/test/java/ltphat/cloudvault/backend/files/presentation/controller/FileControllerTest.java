package ltphat.cloudvault.backend.files.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ltphat.cloudvault.backend.files.application.dto.FileDto;
import ltphat.cloudvault.backend.files.application.dto.UpdateFileRequest;
import ltphat.cloudvault.backend.files.application.service.IFileService;
import ltphat.cloudvault.backend.iam.application.dto.UserDto;
import ltphat.cloudvault.backend.iam.application.service.IAuthService;
import ltphat.cloudvault.backend.shared.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
@AutoConfigureMockMvc(addFilters = false)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IFileService fileService;

    @MockBean
    private IAuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        UserDto user = UserDto.builder().id(userId).email("test@example.com").build();
        when(authService.getMe(anyString())).thenReturn(user);
    }

    @Test
    @WithMockUser
    void getFile_ReturnsFile() throws Exception {
        UUID fileId = UUID.randomUUID();
        FileDto response = FileDto.builder().id(fileId).name("test.txt").build();

        when(fileService.getFile(any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/files/{id}", fileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("test.txt"));
    }

    @Test
    @WithMockUser
    void updateFileMetadata_ReturnsUpdated() throws Exception {
        UUID fileId = UUID.randomUUID();
        UpdateFileRequest request = UpdateFileRequest.builder().name("new.txt").build();
        FileDto response = FileDto.builder().id(fileId).name("new.txt").build();

        when(fileService.updateFileMetadata(any(), any(), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/files/{id}", fileId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("new.txt"));
    }
}
