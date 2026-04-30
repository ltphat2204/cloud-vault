package ltphat.cloudvault.backend.iam.application.service.impl;

import lombok.extern.slf4j.Slf4j;
import ltphat.cloudvault.backend.iam.application.dto.*;
import ltphat.cloudvault.backend.iam.application.mapper.AuthApplicationMapper;
import ltphat.cloudvault.backend.iam.application.mapper.ManualAuthApplicationMapper;
import ltphat.cloudvault.backend.iam.application.service.IAuthService;
import ltphat.cloudvault.backend.iam.domain.exception.*;
import ltphat.cloudvault.backend.iam.domain.model.PasswordResetToken;
import ltphat.cloudvault.backend.iam.domain.model.RefreshToken;
import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.iam.domain.model.VerificationToken;
import ltphat.cloudvault.backend.iam.domain.repository.IPasswordResetTokenRepository;
import ltphat.cloudvault.backend.iam.domain.repository.IRefreshTokenRepository;
import ltphat.cloudvault.backend.iam.domain.repository.IUserRepository;
import ltphat.cloudvault.backend.iam.domain.repository.IVerificationTokenRepository;
import ltphat.cloudvault.backend.mail.domain.model.MailRequest;
import ltphat.cloudvault.backend.mail.domain.service.MailService;
import ltphat.cloudvault.backend.shared.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    private final IUserRepository userRepository;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final IVerificationTokenRepository verificationTokenRepository;
    private final IPasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MailService mailService;
    private final AuthApplicationMapper authMapper = new ManualAuthApplicationMapper();

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public AuthServiceImpl(
            IUserRepository userRepository,
            IRefreshTokenRepository refreshTokenRepository,
            IVerificationTokenRepository verificationTokenRepository,
            IPasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            MailService mailService
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailService = mailService;
    }

    @Override
    public AuthResult login(LoginRequest request, String deviceId, String ipAddress) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isVerified()) {
            throw new UserNotVerifiedException();
        }

        return generateAuthResponse(user, deviceId, ipAddress);
    }

    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = User.createNew(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName()
        );

        User savedUser = userRepository.save(user);
        
        sendVerificationEmail(savedUser);
        
        return authMapper.toDto(savedUser);
    }

    @Override
    public AuthResult refresh(String accessToken, String refreshToken, String deviceId, String ipAddress) {
        // 1. Verify Refresh Token is in Redis
        RefreshToken storedRt = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new TokenSecurityException("Refresh token not found or already used"));

        // 2. Validate Linkage (AT + Device ID)
        if (!storedRt.getAccessToken().equals(accessToken)) {
            // Potential reuse or theft attempt
            refreshTokenRepository.deleteByUserEmail(storedRt.getUserEmail());
            throw new TokenSecurityException("Token linkage mismatch. All sessions revoked for security.");
        }

        if (!storedRt.getDeviceId().equals(deviceId)) {
            throw new TokenSecurityException("Device mismatch");
        }

        // 3. Extract User
        User user = userRepository.findByEmail(storedRt.getUserEmail())
                .orElseThrow(() -> new TokenSecurityException("User not found"));

        // 4. Rotate: Delete old, generate new
        refreshTokenRepository.deleteByToken(refreshToken);
        
        return generateAuthResponse(user, deviceId, ipAddress);
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    @Override
    public UserDto getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return authMapper.toDto(user);
    }

    @Override
    @Transactional
    public void verify(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(InvalidTokenException::new);

        if (verificationToken.isExpired()) {
            throw new ExpiredTokenException();
        }

        User user = userRepository.findByEmail(verificationToken.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.save(user.markAsVerified());
        verificationTokenRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            if (!user.isVerified()) {
                throw new UserNotVerifiedException();
            }
            sendPasswordResetEmail(user);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(InvalidTokenException::new);

        if (resetToken.isExpired()) {
            throw new ExpiredTokenException();
        }

        User user = userRepository.findByEmail(resetToken.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.save(user.updatePasswordHash(passwordEncoder.encode(request.getNewPassword())));
        passwordResetTokenRepository.deleteByToken(request.getToken());
    }

    @Override
    @Transactional
    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isVerified()) {
            throw new RuntimeException("User is already verified");
        }

        sendVerificationEmail(user);
    }

    private void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .userEmail(user.getEmail())
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        verificationTokenRepository.deleteByUserEmail(user.getEmail());
        verificationTokenRepository.save(verificationToken);

        String verificationLink = frontendUrl + "/verify-email?token=" + token;
        
        String body = String.format(
                "<div style=\"font-family: sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">" +
                "<h2 style=\"color: #2d3748;\">Welcome to CloudVault!</h2>" +
                "<p>Hello <strong>%s</strong>,</p>" +
                "<p>Thank you for joining CloudVault. To get started, please verify your email address by clicking the button below:</p>" +
                "<div style=\"text-align: center; margin: 30px 0;\">" +
                "<a href=\"%s\" style=\"background-color: #4a5568; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold;\">Verify Email Address</a>" +
                "</div>" +
                "<p style=\"color: #718096; font-size: 0.875rem;\">If the button above doesn't work, copy and paste this link into your browser:</p>" +
                "<p style=\"color: #3182ce; font-size: 0.875rem; word-break: break-all;\">%s</p>" +
                "<hr style=\"border: 0; border-top: 1px solid #e0e0e0; margin: 20px 0;\">" +
                "<p style=\"color: #a0aec0; font-size: 0.75rem;\">If you did not create an account, please ignore this email.</p>" +
                "</div>",
                user.getName(), verificationLink, verificationLink
        );

        mailService.sendMail(MailRequest.builder()
                .to(user.getEmail())
                .subject("Verify your CloudVault account")
                .body(body)
                .isHtml(true)
                .build());
    }

    private void sendPasswordResetEmail(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userEmail(user.getEmail())
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        passwordResetTokenRepository.deleteByUserEmail(user.getEmail());
        passwordResetTokenRepository.save(resetToken);

        String resetLink = frontendUrl + "/reset-password?token=" + token;

        String body = String.format(
                "<div style=\"font-family: sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">" +
                "<h2 style=\"color: #2d3748;\">Reset Your Password</h2>" +
                "<p>Hello <strong>%s</strong>,</p>" +
                "<p>We received a request to reset your CloudVault password. Click the button below to choose a new one:</p>" +
                "<div style=\"text-align: center; margin: 30px 0;\">" +
                "<a href=\"%s\" style=\"background-color: #4a5568; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold;\">Reset Password</a>" +
                "</div>" +
                "<p style=\"color: #718096; font-size: 0.875rem;\">This link will expire in 1 hour. If the button above doesn't work, copy and paste this link into your browser:</p>" +
                "<p style=\"color: #3182ce; font-size: 0.875rem; word-break: break-all;\">%s</p>" +
                "<hr style=\"border: 0; border-top: 1px solid #e0e0e0; margin: 20px 0;\">" +
                "<p style=\"color: #a0aec0; font-size: 0.75rem;\">If you did not request a password reset, please ignore this email.</p>" +
                "</div>",
                user.getName(), resetLink, resetLink
        );

        mailService.sendMail(MailRequest.builder()
                .to(user.getEmail())
                .subject("Reset your CloudVault password")
                .body(body)
                .isHtml(true)
                .build());
    }

    private AuthResult generateAuthResponse(User user, String deviceId, String ipAddress) {
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        RefreshToken refreshTokenModel = RefreshToken.builder()
                .token(newRefreshToken)
                .accessToken(newAccessToken)
                .userEmail(user.getEmail())
                .deviceId(deviceId)
                .ipAddress(ipAddress)
                .expiryDate(LocalDateTime.now().plusNanos(refreshExpiration * 1000000))
                .build();

        refreshTokenRepository.save(refreshTokenModel);

        return AuthResult.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(authMapper.toDto(user))
                .build();
    }
}
