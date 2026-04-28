package ltphat.cloudvault.backend.iam.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ltphat.cloudvault.backend.iam.application.dto.*;
import ltphat.cloudvault.backend.iam.application.service.IAuthService;
import ltphat.cloudvault.backend.shared.security.JwtAuthenticationFilter;
import ltphat.cloudvault.backend.shared.security.JwtTokenProvider;
import ltphat.cloudvault.backend.iam.infrastructure.security.SecurityConfig;
import ltphat.cloudvault.backend.iam.infrastructure.security.UserPrincipal;
import ltphat.cloudvault.backend.shared.utils.CookieUtils;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAuthService authService;

    @MockitoBean
    private CookieUtils cookieUtils;

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

        when(jwtTokenProvider.extractAccessTokenUsername(anyString())).thenReturn("test@example.com");
        when(jwtTokenProvider.isAccessTokenValid(anyString(), anyString())).thenReturn(true);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(principal);
    }

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest("test@example.com", "password", "Test User");
        UserDto userDto = UserDto.builder().email("test@example.com").name("Test User").build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(userDto);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "password");
        AuthResult result = AuthResult.builder()
                .accessToken("at")
                .refreshToken("rt")
                .user(UserDto.builder().email("test@example.com").build())
                .build();

        when(authService.login(any(LoginRequest.class), anyString(), anyString())).thenReturn(result);

        mockMvc.perform(post("/auth/login")
                        .header("X-Device-Id", "device1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("at"));
        
        verify(cookieUtils).createCookie(any(), eq("rt"));
    }

    @Test
    void getMe_Success() throws Exception {
        UserDto userDto = UserDto.builder().email("test@example.com").name("Test User").build();
        when(authService.getMe("test@example.com")).thenReturn(userDto);

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    void verify_Success() throws Exception {
        mockMvc.perform(post("/auth/verify")
                        .with(csrf())
                        .param("token", "some-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email verified successfully"));

        verify(authService).verify("some-token");
    }
}
