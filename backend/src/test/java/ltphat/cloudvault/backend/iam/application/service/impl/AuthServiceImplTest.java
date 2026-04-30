package ltphat.cloudvault.backend.iam.application.service.impl;

import ltphat.cloudvault.backend.iam.application.dto.AuthResult;
import ltphat.cloudvault.backend.iam.application.dto.LoginRequest;
import ltphat.cloudvault.backend.iam.application.mapper.AuthApplicationMapper;
import ltphat.cloudvault.backend.iam.domain.exception.InvalidCredentialsException;
import ltphat.cloudvault.backend.iam.domain.exception.TokenSecurityException;
import ltphat.cloudvault.backend.iam.domain.model.RefreshToken;
import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.iam.domain.repository.IRefreshTokenRepository;
import ltphat.cloudvault.backend.iam.domain.repository.IUserRepository;
import ltphat.cloudvault.backend.iam.domain.repository.IVerificationTokenRepository;
import ltphat.cloudvault.backend.iam.domain.repository.IPasswordResetTokenRepository;
import ltphat.cloudvault.backend.mail.domain.service.MailService;
import ltphat.cloudvault.backend.shared.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IRefreshTokenRepository refreshTokenRepository;
    @Mock
    private IVerificationTokenRepository verificationTokenRepository;
    @Mock
    private IPasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private MailService mailService;
    @Mock
    private AuthApplicationMapper authMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshExpiration", 604800000L);
        ReflectionTestUtils.setField(authService, "frontendUrl", "http://localhost:3000");
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("test@example.com", "password");
        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hashed")
                .isVerified(true)
                .build();
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(anyString())).thenReturn("at");
        when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn("rt");
        
        AuthResult result = authService.login(request, "device1", "127.0.0.1");
        
        assertNotNull(result);
        assertEquals("at", result.getAccessToken());
        assertEquals("rt", result.getRefreshToken());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void login_UserNotVerified() {
        LoginRequest request = new LoginRequest("test@example.com", "password");
        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hashed")
                .isVerified(false)
                .build();
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        assertThrows(ltphat.cloudvault.backend.iam.domain.exception.UserNotVerifiedException.class, 
                () -> authService.login(request, "device1", "127.0.0.1"));
    }

    @Test
    void register_Success() {
        ltphat.cloudvault.backend.iam.application.dto.RegisterRequest request = 
                new ltphat.cloudvault.backend.iam.application.dto.RegisterRequest("test@example.com", "password", "Name");
        User user = User.builder().email("test@example.com").name("Name").build();
        
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        authService.register(request);
        
        verify(userRepository).save(any(User.class));
        verify(verificationTokenRepository).save(any(ltphat.cloudvault.backend.iam.domain.model.VerificationToken.class));
        verify(mailService).sendMail(any());
    }

    @Test
    void verify_Success() {
        String token = "valid-token";
        ltphat.cloudvault.backend.iam.domain.model.VerificationToken vt = 
                ltphat.cloudvault.backend.iam.domain.model.VerificationToken.builder()
                .token(token)
                .userEmail("test@example.com")
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        User user = User.builder().email("test@example.com").build();
        
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(vt));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        
        authService.verify(token);
        
        verify(userRepository).save(argThat(u -> u.isVerified()));
        verify(verificationTokenRepository).deleteByToken(token);
    }

    @Test
    void forgotPassword_Success() {
        ltphat.cloudvault.backend.iam.application.dto.ForgotPasswordRequest request = 
                new ltphat.cloudvault.backend.iam.application.dto.ForgotPasswordRequest("test@example.com");
        User user = User.builder().email("test@example.com").isVerified(true).build();
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        
        authService.forgotPassword(request);
        
        verify(passwordResetTokenRepository).save(any());
        verify(mailService).sendMail(any());
    }

    @Test
    void resetPassword_Success() {
        ltphat.cloudvault.backend.iam.application.dto.ResetPasswordRequest request = 
                new ltphat.cloudvault.backend.iam.application.dto.ResetPasswordRequest("token", "new-password");
        ltphat.cloudvault.backend.iam.domain.model.PasswordResetToken prt = 
                ltphat.cloudvault.backend.iam.domain.model.PasswordResetToken.builder()
                .token("token")
                .userEmail("test@example.com")
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        User user = User.builder().email("test@example.com").build();
        
        when(passwordResetTokenRepository.findByToken("token")).thenReturn(Optional.of(prt));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-password")).thenReturn("new-hashed");
        
        authService.resetPassword(request);
        
        verify(userRepository).save(argThat(u -> u.getPasswordHash().equals("new-hashed")));
        verify(passwordResetTokenRepository).deleteByToken("token");
    }

    @Test
    void login_InvalidCredentials() {
        LoginRequest request = new LoginRequest("test@example.com", "password");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request, "device1", "127.0.0.1"));
    }

    @Test
    void refresh_Success() {
        String oldAt = "old-at";
        String oldRt = "old-rt";
        String deviceId = "device1";
        
        RefreshToken storedRt = RefreshToken.builder()
                .token(oldRt)
                .accessToken(oldAt)
                .userEmail("test@example.com")
                .deviceId(deviceId)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        
        User user = User.builder().email("test@example.com").build();
        
        when(refreshTokenRepository.findByToken(oldRt)).thenReturn(Optional.of(storedRt));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(anyString())).thenReturn("new-at");
        when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn("new-rt");
        
        AuthResult result = authService.refresh(oldAt, oldRt, deviceId, "127.0.0.1");
        
        assertNotNull(result);
        assertEquals("new-at", result.getAccessToken());
        verify(refreshTokenRepository).deleteByToken(oldRt);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void refresh_LinkageMismatch_RevokesAll() {
        String oldAt = "stolen-at";
        String oldRt = "old-rt";
        
        RefreshToken storedRt = RefreshToken.builder()
                .token(oldRt)
                .accessToken("original-at")
                .userEmail("test@example.com")
                .build();
        
        when(refreshTokenRepository.findByToken(oldRt)).thenReturn(Optional.of(storedRt));
        
        assertThrows(TokenSecurityException.class, () -> authService.refresh(oldAt, oldRt, "device1", "127.0.0.1"));
        verify(refreshTokenRepository).deleteByUserEmail("test@example.com");
    }
}
