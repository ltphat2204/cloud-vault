package ltphat.cloudvault.backend.audit.application.service.impl;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.audit.application.dto.ActivityLogDto;
import ltphat.cloudvault.backend.audit.application.mapper.ActivityLogApplicationMapper;
import ltphat.cloudvault.backend.audit.application.service.IActivityLogService;
import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ActivityLog;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import ltphat.cloudvault.backend.audit.domain.repository.IActivityLogRepository;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import ltphat.cloudvault.backend.shares.domain.repository.ShareRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements IActivityLogService {

    private final IActivityLogRepository activityLogRepository;
    private final ActivityLogApplicationMapper mapper;
    private final IProjectRepository projectRepository;
    private final IFolderRepository folderRepository;
    private final IFileRepository fileRepository;
    private final ShareRepository shareRepository;

    @Override
    public void logActivity(UUID userId, ActivityAction action, ResourceType resourceType, UUID resourceId, Map<String, Object> details) {
        ActivityLog log = ActivityLog.builder()
                .userId(userId)
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .details(details)
                .build();
        activityLogRepository.save(log);
    }

    @Override
    public Page<ActivityLogDto> getUserActivityLogs(UUID userId, ActivityAction action, ResourceType resourceType, Pageable pageable) {
        return activityLogRepository.findByUserId(userId, action, resourceType, pageable)
                .map(mapper::toDto);
    }

    @Override
    public Page<ActivityLogDto> getResourceActivityLogs(UUID resourceId, ResourceType resourceType, UUID userId, Pageable pageable) {
        if (!hasAccess(resourceId, resourceType, userId)) {
            throw new AccessDeniedException("You do not have permission to view history for this resource");
        }
        
        return activityLogRepository.findByResourceIdAndResourceType(resourceId, resourceType, pageable)
                .map(mapper::toDto);
    }

    private boolean hasAccess(UUID resourceId, ResourceType type, UUID userId) {
        // 1. Check ownership
        boolean isOwner = switch (type) {
            case PROJECT -> projectRepository.findById(resourceId)
                    .map(p -> p.getOwnerId().equals(userId))
                    .orElse(false);
            case FOLDER -> folderRepository.findById(resourceId)
                    .map(f -> f.getOwnerId().equals(userId))
                    .orElse(false);
            case FILE -> fileRepository.findById(resourceId)
                    .map(f -> f.getOwnerId().equals(userId))
                    .orElse(false);
        };
        
        if (isOwner) return true;

        // 2. Check shared access
        try {
            ltphat.cloudvault.backend.shares.domain.model.ResourceType shareType = 
                    ltphat.cloudvault.backend.shares.domain.model.ResourceType.valueOf(type.name());
            return shareRepository.existsByResourceAndUser(shareType, resourceId, userId);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
