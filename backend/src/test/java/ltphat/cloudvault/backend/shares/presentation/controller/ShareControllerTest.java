package ltphat.cloudvault.backend.shares.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ltphat.cloudvault.backend.iam.application.service.IAuthService;
import ltphat.cloudvault.backend.iam.infrastructure.security.SecurityConfig;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
import ltphat.cloudvault.backend.shared.security.JwtAuthenticationFilter;
import ltphat.cloudvault.backend.shared.security.JwtTokenProvider;
import ltphat.cloudvault.backend.shares.application.dto.ShareResourceRequest;
import ltphat.cloudvault.backend.shares.application.dto.ShareResponse;
import ltphat.cloudvault.backend.shares.application.service.ShareService;
import ltphat.cloudvault.backend.shares.domain.model.Permission;
import ltphat.cloudvault.backend.shares.domain.model.ResourceType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShareController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@AutoConfigureMockMvc
class ShareControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ShareService shareService;
    @MockitoBean private IAuthService authService;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private UserDetailsService userDetailsService;
    @Autowired private ObjectMapper objectMapper;

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

        when(jwtTokenProvider.extractAccessTokenUsername(anyString())).thenReturn("test@example.com");
        when(jwtTokenProvider.isAccessTokenValid(anyString(), anyString())).thenReturn(true);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(principal);
    }

    @Test
    void shareResource_Success() throws Exception {
        ShareResourceRequest request = ShareResourceRequest.builder()
                .resourceType(ResourceType.PROJECT)
                .resourceId(UUID.randomUUID())
                .userEmail("recipient@example.com")
                .permission(Permission.VIEW)
                .build();

        ShareResponse response = ShareResponse.builder()
                .id(UUID.randomUUID())
                .resourceType(ResourceType.PROJECT)
                .build();

        when(shareService.shareResource(any(), any())).thenReturn(response);

        mockMvc.perform(post("/shares")
                        .header("Authorization", "Bearer dummy-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Resource shared successfully"));
    }
}
