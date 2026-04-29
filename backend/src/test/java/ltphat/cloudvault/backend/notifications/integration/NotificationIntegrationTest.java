package ltphat.cloudvault.backend.notifications.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.iam.domain.repository.IUserRepository;
import ltphat.cloudvault.backend.notifications.application.service.NotificationService;
import ltphat.cloudvault.backend.notifications.domain.model.NotificationType;
import ltphat.cloudvault.backend.notifications.domain.repository.NotificationRepository;
import ltphat.cloudvault.backend.shared.AbstractIntegrationTest;
import ltphat.cloudvault.backend.shared.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class NotificationIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private IUserRepository userRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("user@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .name("Test User")
                .build();
        user = userRepository.save(user);
        token = jwtTokenProvider.generateAccessToken(user.getEmail());
    }

    @Test
    void notificationLifecycle() throws Exception {
        // 1. Create a notification
        notificationService.createNotification(user.getId(), NotificationType.SHARE_RECEIVED, "You have a new share", Map.of("resourceId", UUID.randomUUID().toString()));

        // 2. List notifications
        String response = mockMvc.perform(get("/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].message").value("You have a new share"))
                .andExpect(jsonPath("$.data.content[0].read").value(false))
                .andReturn().getResponse().getContentAsString();

        String notificationId = objectMapper.readTree(response).get("data").get("content").get(0).get("id").asText();

        // 3. Mark as read
        mockMvc.perform(patch("/notifications/" + notificationId + "/read")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 4. Verify is read
        mockMvc.perform(get("/notifications")
                        .header("Authorization", "Bearer " + token)
                        .param("unreadOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    void markAllAsRead() throws Exception {
        notificationService.createNotification(user.getId(), NotificationType.SYSTEM_ALERT, "Alert 1", Map.of());
        notificationService.createNotification(user.getId(), NotificationType.SYSTEM_ALERT, "Alert 2", Map.of());

        mockMvc.perform(patch("/notifications/read-all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/notifications")
                        .header("Authorization", "Bearer " + token)
                        .param("unreadOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    void security_OtherUserNotification() throws Exception {
        User otherUser = User.builder()
                .email("other@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .name("Other User")
                .build();
        otherUser = userRepository.save(otherUser);
        
        notificationService.createNotification(otherUser.getId(), NotificationType.SHARE_RECEIVED, "Secret", Map.of());
        UUID otherNotifId = notificationRepository.findAllByUserId(otherUser.getId(), org.springframework.data.domain.Pageable.unpaged()).getContent().get(0).getId();

        // User A tries to read User B's notification
        mockMvc.perform(patch("/notifications/" + otherNotifId + "/read")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
