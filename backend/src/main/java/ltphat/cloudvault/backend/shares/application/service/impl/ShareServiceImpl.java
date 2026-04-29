package ltphat.cloudvault.backend.shares.application.service.impl;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.iam.domain.model.User;
import ltphat.cloudvault.backend.iam.domain.repository.IUserRepository;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import ltphat.cloudvault.backend.shares.application.dto.*;
import ltphat.cloudvault.backend.shares.application.service.ShareService;
import ltphat.cloudvault.backend.shares.domain.exception.ShareException;
import ltphat.cloudvault.backend.shares.domain.exception.ShareNotFoundException;
import ltphat.cloudvault.backend.shares.domain.model.ResourceType;
import ltphat.cloudvault.backend.shares.domain.model.Share;
import ltphat.cloudvault.backend.shares.domain.repository.ShareRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final ShareRepository shareRepository;
    private final IUserRepository userRepository;
    private final IProjectRepository projectRepository;
    private final IFolderRepository folderRepository;
    private final IFileRepository fileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ShareResponse shareResource(ShareResourceRequest request, UUID requesterId) {
        validateResourceOwnership(request.getResourceType(), request.getResourceId(), requesterId);

        User recipient = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new ShareException("User with email " + request.getUserEmail() + " not found"));

        if (recipient.getId().equals(requesterId)) {
            throw new ShareException("You cannot share a resource with yourself");
        }

        if (shareRepository.existsByResourceAndUser(request.getResourceType(), request.getResourceId(), recipient.getId())) {
            throw new ShareException("Resource already shared with this user");
        }

        Share share = Share.createInternal(
                request.getResourceType(),
                request.getResourceId(),
                recipient.getId(),
                request.getPermission()
        );

        Share saved = shareRepository.save(share);
        return mapToResponse(saved, recipient, null);
    }

    @Override
    @Transactional
    public ShareResponse createPublicLink(CreatePublicLinkRequest request, UUID requesterId) {
        validateResourceOwnership(request.getResourceType(), request.getResourceId(), requesterId);

        String passwordHash = (request.getPassword() != null && !request.getPassword().isEmpty())
                ? passwordEncoder.encode(request.getPassword())
                : null;

        Share share = Share.createPublic(
                request.getResourceType(),
                request.getResourceId(),
                passwordHash,
                request.getExpiresAt()
        );

        Share saved = shareRepository.save(share);
        return mapToResponse(saved, null, null);
    }

    @Override
    @Transactional
    public void updateShare(UUID shareId, UpdateShareRequest request, UUID requesterId) {
        Share share = shareRepository.findById(shareId)
                .orElseThrow(() -> new ShareNotFoundException("Share record not found"));

        validateResourceOwnership(share.getResourceType(), share.getResourceId(), requesterId);

        share.updatePermission(request.getPermission());
        shareRepository.save(share);
    }

    @Override
    @Transactional
    public void revokeShare(UUID shareId, UUID requesterId) {
        Share share = shareRepository.findById(shareId)
                .orElseThrow(() -> new ShareNotFoundException("Share record not found"));

        validateResourceOwnership(share.getResourceType(), share.getResourceId(), requesterId);

        shareRepository.delete(shareId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShareResponse> getSharesForResource(ResourceType type, UUID resourceId, UUID requesterId) {
        validateResourceOwnership(type, resourceId, requesterId);

        return shareRepository.findByResource(type, resourceId).stream()
                .map(share -> {
                    User recipient = share.getSharedWithUserId() != null
                            ? userRepository.findById(share.getSharedWithUserId()).orElse(null)
                            : null;
                    return mapToResponse(share, recipient, null);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShareResponse> getResourcesSharedWithMe(UUID userId) {
        return shareRepository.findBySharedWithUserId(userId).stream()
                .map(share -> {
                    String resourceName = getResourceName(share.getResourceType(), share.getResourceId());
                    String sharerEmail = getResourceOwnerEmail(share.getResourceType(), share.getResourceId());
                    ShareResponse response = mapToResponse(share, null, resourceName);
                    response.setSharedBy(sharerEmail);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShareResponse getPublicShare(UUID token, String password) {
        Share share = shareRepository.findByAccessToken(token)
                .orElseThrow(() -> new ShareNotFoundException("Public link not found or invalid"));

        if (share.isExpired()) {
            throw new ShareException("This public link has expired");
        }

        if (share.getPasswordHash() != null) {
            if (password == null || !passwordEncoder.matches(password, share.getPasswordHash())) {
                throw new ShareException("Invalid password for public link");
            }
        }

        String resourceName = getResourceName(share.getResourceType(), share.getResourceId());
        return mapToResponse(share, null, resourceName);
    }

    private void validateResourceOwnership(ResourceType type, UUID resourceId, UUID ownerId) {
        boolean isOwner = switch (type) {
            case PROJECT -> projectRepository.findById(resourceId)
                    .map(p -> p.getOwnerId().equals(ownerId))
                    .orElse(false);
            case FOLDER -> folderRepository.findById(resourceId)
                    .map(f -> f.getOwnerId().equals(ownerId))
                    .orElse(false);
            case FILE -> fileRepository.findById(resourceId)
                    .map(f -> f.getOwnerId().equals(ownerId))
                    .orElse(false);
        };

        if (!isOwner) {
            throw new ShareException("You do not have permission to share this resource");
        }
    }

    private String getResourceName(ResourceType type, UUID resourceId) {
        return switch (type) {
            case PROJECT -> projectRepository.findById(resourceId).map(Project::getName).orElse("Unknown Project");
            case FOLDER -> folderRepository.findById(resourceId).map(Folder::getName).orElse("Unknown Folder");
            case FILE -> fileRepository.findById(resourceId).map(File::getName).orElse("Unknown File");
        };
    }

    private String getResourceOwnerEmail(ResourceType type, UUID resourceId) {
        UUID ownerId = switch (type) {
            case PROJECT -> projectRepository.findById(resourceId).map(Project::getOwnerId).orElse(null);
            case FOLDER -> folderRepository.findById(resourceId).map(Folder::getOwnerId).orElse(null);
            case FILE -> fileRepository.findById(resourceId).map(File::getOwnerId).orElse(null);
        };

        if (ownerId == null) return "Unknown";
        return userRepository.findById(ownerId).map(User::getEmail).orElse("Unknown");
    }

    private ShareResponse mapToResponse(Share share, User recipient, String resourceName) {
        ShareResponse.SharedUserDto userDto = recipient != null
                ? ShareResponse.SharedUserDto.builder()
                .id(recipient.getId())
                .email(recipient.getEmail())
                .build()
                : null;

        String publicUrl = share.getAccessToken() != null
                ? "https://cloudvault.com/s/" + share.getAccessToken()
                : null;

        return ShareResponse.builder()
                .id(share.getId())
                .resourceType(share.getResourceType())
                .resourceId(share.getResourceId())
                .resourceName(resourceName)
                .sharedWithUser(userDto)
                .permission(share.getPermission())
                .accessToken(share.getAccessToken())
                .publicUrl(publicUrl)
                .expiresAt(share.getExpiresAt())
                .createdAt(share.getCreatedAt())
                .build();
    }
}
