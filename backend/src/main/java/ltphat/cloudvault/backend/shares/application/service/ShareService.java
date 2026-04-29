package ltphat.cloudvault.backend.shares.application.service;

import ltphat.cloudvault.backend.shares.application.dto.*;
import ltphat.cloudvault.backend.shares.domain.model.ResourceType;

import java.util.List;
import java.util.UUID;

public interface ShareService {
    ShareResponse shareResource(ShareResourceRequest request, UUID requesterId);
    ShareResponse createPublicLink(CreatePublicLinkRequest request, UUID requesterId);
    void updateShare(UUID shareId, UpdateShareRequest request, UUID requesterId);
    void revokeShare(UUID shareId, UUID requesterId);
    List<ShareResponse> getSharesForResource(ResourceType type, UUID resourceId, UUID requesterId);
    List<ShareResponse> getResourcesSharedWithMe(UUID userId);
    ShareResponse getPublicShare(UUID token, String password);
}
