package ltphat.cloudvault.backend.iam.application.service;

import ltphat.cloudvault.backend.iam.application.dto.LoginRequest;
import ltphat.cloudvault.backend.iam.application.dto.RegisterRequest;
import ltphat.cloudvault.backend.iam.application.dto.UserDto;
import ltphat.cloudvault.backend.iam.application.dto.AuthResult;

public interface IAuthService {
    AuthResult login(LoginRequest request, String deviceId, String ipAddress);
    UserDto register(RegisterRequest request);
    AuthResult refresh(String accessToken, String refreshToken, String deviceId, String ipAddress);
    void logout(String accessToken, String refreshToken);
    UserDto getMe(String email);
    void verify(String token);
}
