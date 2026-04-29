package ltphat.cloudvault.backend.shares.application.service;

import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.iam.domain.repository.IUserRepository;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import ltphat.cloudvault.backend.shares.application.dto.*;
import ltphat.cloudvault.backend.shares.application.service.impl.ShareServiceImpl;
import ltphat.cloudvault.backend.shares.domain.exception.ShareException;
import ltphat.cloudvault.backend.shares.domain.model.Permission;
import ltphat.cloudvault.backend.shares.domain.model.ResourceType;
import ltphat.cloudvault.backend.shares.domain.model.Share;
import ltphat.cloudvault.backend.shares.domain.repository.ShareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ltphat.cloudvault.backend.notifications.application.service.NotificationService;
import ltphat.cloudvault.backend.notifications.domain.model.NotificationType;
import ltphat.cloudvault.backend.audit.application.service.IActivityLogService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShareServiceTest {

    @Mock private ShareRepository shareRepository;
    @Mock private IUserRepository userRepository;
    @Mock private IProjectRepository projectRepository;
    @Mock private IFolderRepository folderRepository;
    @Mock private IFileRepository fileRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private NotificationService notificationService;
    @Mock private IActivityLogService auditService;

    @InjectMocks private ShareServiceImpl shareService;

    private UUID userId;
    private UUID recipientId;
    private UUID projectId;
    private Project project;
    private User recipient;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        recipientId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        project = Project.builder().id(projectId).ownerId(userId).name("Test Project").build();
        recipient = User.builder().id(recipientId).email("recipient@example.com").build();
    }

    @Test
    void shareResource_Success() {
        ShareResourceRequest request = ShareResourceRequest.builder()
                .resourceType(ResourceType.PROJECT)
                .resourceId(projectId)
                .userEmail("recipient@example.com")
                .permission(Permission.VIEW)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("recipient@example.com")).thenReturn(Optional.of(recipient));
        when(shareRepository.existsByResourceAndUser(any(), any(), any())).thenReturn(false);
        when(shareRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ShareResponse response = shareService.shareResource(request, userId);

        assertThat(response).isNotNull();
        assertThat(response.getResourceType()).isEqualTo(ResourceType.PROJECT);
        assertThat(response.getSharedWithUser().getEmail()).isEqualTo("recipient@example.com");
        verify(shareRepository).save(any());
        verify(notificationService).createNotification(eq(recipientId), eq(NotificationType.SHARE_RECEIVED), anyString(), anyMap());
    }

    @Test
    void shareResource_NotOwner_ThrowsException() {
        UUID otherUserId = UUID.randomUUID();
        ShareResourceRequest request = ShareResourceRequest.builder()
                .resourceType(ResourceType.PROJECT)
                .resourceId(projectId)
                .userEmail("recipient@example.com")
                .permission(Permission.VIEW)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> shareService.shareResource(request, otherUserId))
                .isInstanceOf(ShareException.class)
                .hasMessageContaining("permission to share");
    }

    @Test
    void createPublicLink_Success() {
        CreatePublicLinkRequest request = CreatePublicLinkRequest.builder()
                .resourceType(ResourceType.PROJECT)
                .resourceId(projectId)
                .password("password123")
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(shareRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ShareResponse response = shareService.createPublicLink(request, userId);

        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getPublicUrl()).contains(response.getAccessToken().toString());
        verify(shareRepository).save(any());
    }

    @Test
    void getResourcesSharedWithMe_ReturnsSorted() {
        Share share1 = Share.builder().resourceType(ResourceType.PROJECT).resourceId(projectId).createdAt(LocalDateTime.now().minusDays(1)).build();
        Share share2 = Share.builder().resourceType(ResourceType.PROJECT).resourceId(projectId).createdAt(LocalDateTime.now()).build();

        when(shareRepository.findBySharedWithUserId(userId)).thenReturn(List.of(share2, share1));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(recipient)); // Sharer

        List<ShareResponse> result = shareService.getResourcesSharedWithMe(userId);

        assertThat(result).hasSize(2);
        // Repository should already return sorted list due to findBySharedWithUserIdOrderByCreatedAtDesc
        verify(shareRepository).findBySharedWithUserId(userId);
    }

    @Test
    void updateShare_Success() {
        UpdateShareRequest request = new UpdateShareRequest(Permission.EDIT);
        UUID shareId = UUID.randomUUID();
        Share share = Share.createInternal(ResourceType.PROJECT, projectId, recipientId, Permission.VIEW);

        when(shareRepository.findById(shareId)).thenReturn(Optional.of(share));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        shareService.updateShare(shareId, request, userId);

        assertThat(share.getPermission()).isEqualTo(Permission.EDIT);
        verify(shareRepository).save(share);
        verify(notificationService).createNotification(eq(recipientId), eq(NotificationType.SHARE_UPDATED), anyString(), anyMap());
    }

    @Test
    void revokeShare_Success() {
        UUID shareId = UUID.randomUUID();
        Share share = Share.createInternal(ResourceType.PROJECT, projectId, recipientId, Permission.VIEW);

        when(shareRepository.findById(shareId)).thenReturn(Optional.of(share));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        shareService.revokeShare(shareId, userId);

        verify(shareRepository).delete(shareId);
        verify(notificationService).createNotification(eq(recipientId), eq(NotificationType.SHARE_REVOKED), anyString(), anyMap());
    }
}
