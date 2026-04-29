package ltphat.cloudvault.backend.search.application.service.impl;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.search.application.dto.SearchResponse;
import ltphat.cloudvault.backend.search.application.service.ISearchService;
import ltphat.cloudvault.backend.search.domain.model.SearchResourceType;
import ltphat.cloudvault.backend.shares.application.service.ShareService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements ISearchService {

    private final IFileRepository fileRepository;
    private final IFolderRepository folderRepository;
    private final ShareService shareService;

    @Override
    public List<SearchResponse> search(String query, UUID projectId, UUID folderId, UUID userId) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<SearchResponse> results = new ArrayList<>();

        if (folderId != null) {
            // Scoped search within a folder
            // First validate access to the project
            if (projectId == null) {
                // If folderId is provided but projectId is not, find projectId from folder
                Folder folder = folderRepository.findById(folderId)
                        .orElseThrow(() -> new RuntimeException("Folder not found"));
                projectId = folder.getProjectId();
            }

            if (!shareService.hasProjectAccess(projectId, userId) && !isProjectOwner(projectId, userId)) {
                throw new AccessDeniedException("Access denied to project");
            }

            results.addAll(searchInFolder(query, folderId));
        } else if (projectId != null) {
            // Scoped search within a project
            if (!shareService.hasProjectAccess(projectId, userId) && !isProjectOwner(projectId, userId)) {
                throw new AccessDeniedException("Access denied to project");
            }

            results.addAll(searchInProject(query, projectId));
        } else {
            // Global search
            List<UUID> accessibleProjectIds = shareService.getAccessibleProjectIds(userId);
            if (!accessibleProjectIds.isEmpty()) {
                results.addAll(searchGlobal(query, accessibleProjectIds));
            }
        }

        return results;
    }

    private boolean isProjectOwner(UUID projectId, UUID userId) {
        // This is a bit inefficient if called many times, but usually ShareService has cached this or it's a simple query
        // Actually, ShareServiceImpl.getAccessibleProjectIds already handles both.
        // Let's just use getAccessibleProjectIds for consistency if needed, 
        // but for a single check, hasProjectAccess might be faster if it's optimized.
        // However, hasProjectAccess in ShareServiceImpl only checks the share_repository.
        // We need to check both owner and shares.
        return shareService.getAccessibleProjectIds(userId).contains(projectId);
    }

    private List<SearchResponse> searchInFolder(String query, UUID folderId) {
        List<SearchResponse> responses = new ArrayList<>();
        
        List<File> files = fileRepository.findByFolderIdAndNameContainingIgnoreCaseAndDeletedAtIsNull(folderId, query);
        responses.addAll(files.stream().map(this::mapToFileResponse).collect(Collectors.toList()));
        
        List<Folder> folders = folderRepository.findByParentFolderIdAndNameContainingIgnoreCaseAndDeletedAtIsNull(folderId, query);
        responses.addAll(folders.stream().map(this::mapToFolderResponse).collect(Collectors.toList()));
        
        return responses;
    }

    private List<SearchResponse> searchInProject(String query, UUID projectId) {
        return searchGlobal(query, List.of(projectId));
    }

    private List<SearchResponse> searchGlobal(String query, List<UUID> projectIds) {
        List<SearchResponse> responses = new ArrayList<>();
        
        List<File> files = fileRepository.findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(projectIds, query);
        responses.addAll(files.stream().map(this::mapToFileResponse).collect(Collectors.toList()));
        
        List<Folder> folders = folderRepository.findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(projectIds, query);
        responses.addAll(folders.stream().map(this::mapToFolderResponse).collect(Collectors.toList()));
        
        return responses;
    }

    private SearchResponse mapToFileResponse(File file) {
        return SearchResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .type(SearchResourceType.FILE)
                .projectId(file.getProjectId())
                .parentId(file.getFolderId())
                .size(file.getSize())
                .mimeType(file.getMimeType())
                .updatedAt(file.getUpdatedAt())
                .ownerId(file.getOwnerId())
                .build();
    }

    private SearchResponse mapToFolderResponse(Folder folder) {
        return SearchResponse.builder()
                .id(folder.getId())
                .name(folder.getName())
                .type(SearchResourceType.FOLDER)
                .projectId(folder.getProjectId())
                .parentId(folder.getParentFolderId())
                .updatedAt(folder.getUpdatedAt())
                .ownerId(folder.getOwnerId())
                .build();
    }
}
