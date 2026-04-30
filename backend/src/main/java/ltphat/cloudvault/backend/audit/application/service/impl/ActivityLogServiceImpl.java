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
import ltphat.cloudvault.backend.shared.dto.CursorPageResponse;
import ltphat.cloudvault.backend.shared.dto.CursorParams;
import ltphat.cloudvault.backend.shared.utils.CursorUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public CursorPageResponse<ActivityLogDto> getUserActivityLogs(UUID userId, ActivityAction action, ResourceType resourceType, CursorParams cursorParams) {
        List<ActivityLog> logs = activityLogRepository.findByUserId(userId, action, resourceType, cursorParams);
        return toCursorPageResponse(logs, cursorParams);
    }

    @Override
    public CursorPageResponse<ActivityLogDto> getResourceActivityLogs(UUID resourceId, ResourceType resourceType, UUID userId, CursorParams cursorParams) {
        if (!hasAccess(resourceId, resourceType, userId)) {
            throw new AccessDeniedException("You do not have permission to view history for this resource");
        }
        
        List<ActivityLog> logs = activityLogRepository.findByResourceIdAndResourceType(resourceId, resourceType, cursorParams);
        return toCursorPageResponse(logs, cursorParams);
    }

    private CursorPageResponse<ActivityLogDto> toCursorPageResponse(List<ActivityLog> logs, CursorParams params) {
        boolean hasNext = logs.size() > params.getPageSize();
        List<ActivityLog> items = hasNext ? logs.subList(0, params.getPageSize()) : logs;
        
        String nextCursor = null;
        if (hasNext && !items.isEmpty()) {
            ActivityLog lastItem = items.get(items.size() - 1);
            if (lastItem.getCreatedAt() != null) {
                String sortField = params.getSortField();
                String fieldValue = "createdAt".equals(sortField) ? lastItem.getCreatedAt().toString() : lastItem.getCreatedAt().toString();
                nextCursor = CursorUtils.encode(fieldValue, lastItem.getId().toString());
            }
        }
        
        return CursorPageResponse.of(items.stream().map(mapper::toDto).toList(), nextCursor, hasNext);
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
