package ltphat.cloudvault.backend.iam.application.service.impl;

import ltphat.cloudvault.backend.iam.application.dto.AuthResult;
import ltphat.cloudvault.backend.iam.application.dto.LoginRequest;
import ltphat.cloudvault.backend.iam.application.dto.RegisterRequest;
import ltphat.cloudvault.backend.iam.application.dto.UserDto;
import ltphat.cloudvault.backend.iam.application.mapper.AuthApplicationMapper;
import ltphat.cloudvault.backend.iam.application.mapper.ManualAuthApplicationMapper;
import ltphat.cloudvault.backend.iam.application.service.IAuthService;
import ltphat.cloudvault.backend.iam.domain.exception.DuplicateEmailException;
import ltphat.cloudvault.backend.iam.domain.exception.InvalidCredentialsException;
import ltphat.cloudvault.backend.iam.domain.exception.TokenSecurityException;
import ltphat.cloudvault.backend.iam.domain.model.RefreshToken;
import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.iam.domain.repository.IRefreshTokenRepository;
import ltphat.cloudvault.backend.iam.domain.repository.IUserRepository;
import ltphat.cloudvault.backend.shared.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements IAuthService {

    private final IUserRepository userRepository;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthApplicationMapper authMapper = new ManualAuthApplicationMapper();

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public AuthServiceImpl(
            IUserRepository userRepository,
            IRefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResult login(LoginRequest request, String deviceId, String ipAddress) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return generateAuthResponse(user, deviceId, ipAddress);
    }

    @Override
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
    public void verify(String token) {
        // TODO: Implement email verification logic
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
