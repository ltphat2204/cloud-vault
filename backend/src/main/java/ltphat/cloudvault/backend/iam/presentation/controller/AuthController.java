package ltphat.cloudvault.backend.iam.presentation.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.iam.application.dto.*;
import ltphat.cloudvault.backend.iam.application.service.IAuthService;
import ltphat.cloudvault.backend.shared.dto.ApiResponse;
import ltphat.cloudvault.backend.shared.utils.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and session management")
public class AuthController {

    private final IAuthService authService;
    private final CookieUtils cookieUtils;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with email, password, and name")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody RegisterRequest request) {
        UserDto user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns an access token. Sets a refresh token in a secure cookie.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @RequestBody LoginRequest request,
            @Parameter(in = ParameterIn.HEADER, name = "X-Device-Id", description = "Unique ID for the device")
            @RequestHeader(value = "X-Device-Id") String deviceId,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse
    ) {
        String ipAddress = servletRequest.getRemoteAddr();
        AuthResult result = authService.login(request, deviceId, ipAddress);
        
        cookieUtils.createCookie(servletResponse, result.getRefreshToken());
        
        AuthResponse response = AuthResponse.builder()
                .accessToken(result.getAccessToken())
                .user(result.getUser())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Uses a valid refresh token from cookies to issue a new access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Parameter(in = ParameterIn.HEADER, name = "X-Device-Id", description = "Unique ID for the device")
            @RequestHeader(value = "X-Device-Id") String deviceId,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse
    ) {
        String accessToken = authHeader.substring(7);
        String refreshToken = cookieUtils.getCookieValue(servletRequest);
        String ipAddress = servletRequest.getRemoteAddr();
        
        AuthResult result = authService.refresh(accessToken, refreshToken, deviceId, ipAddress);
        
        cookieUtils.createCookie(servletResponse, result.getRefreshToken());
        
        AuthResponse response = AuthResponse.builder()
                .accessToken(result.getAccessToken())
                .user(result.getUser())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidates the current access and refresh tokens")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse
    ) {
        String accessToken = authHeader.substring(7);
        String refreshToken = cookieUtils.getCookieValue(servletRequest);
        
        authService.logout(accessToken, refreshToken);
        cookieUtils.deleteCookie(servletResponse);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieves information about the currently authenticated user")
    public ResponseEntity<ApiResponse<UserDto>> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        UserDto user = authService.getMe(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify account", description = "Verifies a user account using a verification token")
    public ResponseEntity<ApiResponse<Void>> verify(@RequestParam String token) {
        authService.verify(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Email verified successfully"));
    }
}
