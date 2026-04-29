package ltphat.cloudvault.backend.audit.application.service.impl;

import ltphat.cloudvault.backend.audit.application.dto.ActivityLogDto;
import ltphat.cloudvault.backend.audit.application.mapper.ActivityLogApplicationMapper;
import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ActivityLog;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import ltphat.cloudvault.backend.audit.domain.repository.IActivityLogRepository;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import ltphat.cloudvault.backend.shares.domain.repository.ShareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceTest {

    @Mock
    private IActivityLogRepository activityLogRepository;
    @Mock
    private ActivityLogApplicationMapper mapper;
    @Mock
    private IProjectRepository projectRepository;
    @Mock
    private IFolderRepository folderRepository;
    @Mock
    private IFileRepository fileRepository;
    @Mock
    private ShareRepository shareRepository;

    private ActivityLogServiceImpl activityLogService;

    @BeforeEach
    void setUp() {
        activityLogService = new ActivityLogServiceImpl(
                activityLogRepository, mapper, projectRepository, folderRepository, fileRepository, shareRepository);
    }

    @Test
    void logActivity_ShouldSaveLog() {
        UUID userId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();
        Map<String, Object> details = Map.of("fileName", "test.txt");

        activityLogService.logActivity(userId, ActivityAction.FILE_UPLOADED, ResourceType.FILE, resourceId, details);

        verify(activityLogRepository, times(1)).save(any(ActivityLog.class));
    }

    @Test
    void getUserActivityLogs_ShouldReturnPage() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Page<ActivityLog> logPage = new PageImpl<>(List.of(ActivityLog.builder().build()));

        when(activityLogRepository.findByUserId(eq(userId), any(), any(), eq(pageable))).thenReturn(logPage);
        when(mapper.toDto(any())).thenReturn(new ActivityLogDto());

        Page<ActivityLogDto> result = activityLogService.getUserActivityLogs(userId, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getResourceActivityLogs_WhenOwner_ShouldReturnHistory() {
        UUID userId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        
        File file = File.builder().id(fileId).ownerId(userId).build();
        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        
        Page<ActivityLog> logPage = new PageImpl<>(List.of(ActivityLog.builder().build()));
        when(activityLogRepository.findByResourceIdAndResourceType(eq(fileId), eq(ResourceType.FILE), eq(pageable)))
                .thenReturn(logPage);
        when(mapper.toDto(any())).thenReturn(new ActivityLogDto());

        Page<ActivityLogDto> result = activityLogService.getResourceActivityLogs(fileId, ResourceType.FILE, userId, pageable);

        assertNotNull(result);
        verify(activityLogRepository).findByResourceIdAndResourceType(fileId, ResourceType.FILE, pageable);
    }

    @Test
    void getResourceActivityLogs_WhenSharedUser_ShouldReturnHistory() {
        UUID userId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        
        // Not owner
        File file = File.builder().id(fileId).ownerId(UUID.randomUUID()).build();
        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        
        // Has share
        when(shareRepository.existsByResourceAndUser(any(), eq(fileId), eq(userId))).thenReturn(true);
        
        Page<ActivityLog> logPage = new PageImpl<>(List.of(ActivityLog.builder().build()));
        when(activityLogRepository.findByResourceIdAndResourceType(eq(fileId), eq(ResourceType.FILE), eq(pageable)))
                .thenReturn(logPage);
        when(mapper.toDto(any())).thenReturn(new ActivityLogDto());

        Page<ActivityLogDto> result = activityLogService.getResourceActivityLogs(fileId, ResourceType.FILE, userId, pageable);

        assertNotNull(result);
    }

    @Test
    void getResourceActivityLogs_WhenNoAccess_ShouldThrowException() {
        UUID userId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        
        // Not owner
        File file = File.builder().id(fileId).ownerId(UUID.randomUUID()).build();
        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        
        // No share
        when(shareRepository.existsByResourceAndUser(any(), eq(fileId), eq(userId))).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> 
                activityLogService.getResourceActivityLogs(fileId, ResourceType.FILE, userId, pageable));
    }
}
