package ltphat.cloudvault.backend.folders.application.service.impl;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.folders.application.dto.CreateFolderRequest;
import ltphat.cloudvault.backend.folders.application.dto.FolderDto;
import ltphat.cloudvault.backend.folders.application.dto.MoveFolderRequest;
import ltphat.cloudvault.backend.folders.application.dto.UpdateFolderRequest;
import ltphat.cloudvault.backend.folders.application.mapper.FolderApplicationMapper;
import ltphat.cloudvault.backend.folders.application.service.IFolderService;
import ltphat.cloudvault.backend.folders.domain.exception.FolderException;
import ltphat.cloudvault.backend.folders.domain.exception.FolderNotFoundException;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import ltphat.cloudvault.backend.audit.application.service.IActivityLogService;
import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import ltphat.cloudvault.backend.notifications.application.service.RealTimeUpdateService;
import ltphat.cloudvault.backend.notifications.domain.model.RealTimeUpdateType;
import ltphat.cloudvault.backend.files.application.service.IStorageService;
import ltphat.cloudvault.backend.shares.application.service.ShareService;
import ltphat.cloudvault.backend.shared.dto.CursorPageResponse;
import ltphat.cloudvault.backend.shared.dto.CursorParams;
import ltphat.cloudvault.backend.shared.utils.CursorUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements IFolderService {

    private final IFolderRepository folderRepository;
    private final IFileRepository fileRepository;
    private final IProjectRepository projectRepository;
    private final FolderApplicationMapper folderApplicationMapper;
    private final IStorageService storageService;
    private final IActivityLogService auditService;
    private final ShareService shareService;
    private final RealTimeUpdateService realTimeUpdateService;

    @Override
    @Transactional
    public FolderDto createFolder(CreateFolderRequest request, UUID ownerId) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(ownerId) && !shareService.hasProjectAccess(request.getProjectId(), ownerId)) {
            throw new AccessDeniedException("You do not have access to this project");
        }

        if (folderRepository.existsByNameAndParentFolderIdAndProjectId(request.getName(), request.getParentFolderId(),
                request.getProjectId())) {
            throw new FolderException("Folder with name '" + request.getName() + "' already exists in this location");
        }

        if (request.getParentFolderId() != null) {
            Folder parent = folderRepository.findById(request.getParentFolderId())
                    .orElseThrow(() -> new FolderNotFoundException(request.getParentFolderId()));
            if (!parent.getProjectId().equals(request.getProjectId())) {
                throw new FolderException("Parent folder must belong to the same project");
            }
        }

        Folder folder = Folder.create(request.getName(), request.getParentFolderId(), request.getProjectId(), ownerId);
        Folder savedFolder = folderRepository.save(folder);

        auditService.logActivity(ownerId, ActivityAction.FOLDER_CREATED, ResourceType.FOLDER, savedFolder.getId(), 
                Map.of("name", savedFolder.getName()));

        broadcastUpdate(request.getProjectId(), RealTimeUpdateType.FOLDER_CREATED, ownerId, 
                Map.of("projectId", request.getProjectId(), "parentFolderId", request.getParentFolderId() != null ? request.getParentFolderId() : "root", 
                        "resourceId", savedFolder.getId(), "resourceName", savedFolder.getName()));

        return folderApplicationMapper.toDto(savedFolder);
    }

    @Override
    public FolderDto getFolder(UUID id, UUID ownerId) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException(id));

        if (!folder.getOwnerId().equals(ownerId) && !shareService.hasProjectAccess(folder.getProjectId(), ownerId)) {
            throw new AccessDeniedException("You do not have access to this folder");
        }

        if (folder.isDeleted()) {
            throw new FolderNotFoundException(id);
        }

        return folderApplicationMapper.toDto(folder);
    }

    @Override
    public CursorPageResponse<FolderDto> listFolders(UUID projectId, UUID parentFolderId, UUID ownerId, CursorParams cursorParams) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(ownerId) && !shareService.hasProjectAccess(projectId, ownerId)) {
            throw new AccessDeniedException("You do not have access to this project");
        }

        List<Folder> folders = folderRepository.findByProjectIdAndParentFolderId(projectId, parentFolderId, cursorParams);
        
        List<Folder> filteredFolders = folders.stream()
                .filter(f -> !f.isDeleted())
                .collect(Collectors.toList());

        return toCursorPageResponse(filteredFolders, cursorParams);
    }

    private CursorPageResponse<FolderDto> toCursorPageResponse(List<Folder> folders, CursorParams params) {
        boolean hasNext = folders.size() > params.getPageSize();
        List<Folder> items = hasNext ? folders.subList(0, params.getPageSize()) : folders;
        
        String nextCursor = null;
        if (hasNext && !items.isEmpty()) {
            Folder lastItem = items.get(items.size() - 1);
            if (lastItem.getCreatedAt() != null) {
                String sortField = params.getSortField();
                String fieldValue = "name".equals(sortField) ? lastItem.getName() : lastItem.getCreatedAt().toString();
                nextCursor = CursorUtils.encode(fieldValue, lastItem.getId().toString());
            }
        }
        
        return CursorPageResponse.of(items.stream().map(folderApplicationMapper::toDto).toList(), nextCursor, hasNext);
    }

    @Override
    @Transactional
    public FolderDto updateFolder(UUID id, UpdateFolderRequest request, UUID ownerId) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException(id));

        if (!folder.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Access denied");
        }

        if (folderRepository.existsByNameAndParentFolderIdAndProjectId(request.getName(), folder.getParentFolderId(),
                folder.getProjectId())) {
            throw new FolderException("Folder with name '" + request.getName() + "' already exists in this location");
        }

        String oldName = folder.getName();
        folder.update(request.getName());
        Folder updatedFolder = folderRepository.save(folder);

        auditService.logActivity(ownerId, ActivityAction.FOLDER_RENAMED, ResourceType.FOLDER, id, 
                Map.of("oldName", oldName, "newName", request.getName()));

        broadcastUpdate(folder.getProjectId(), RealTimeUpdateType.FOLDER_UPDATED, ownerId, 
                Map.of("projectId", folder.getProjectId(), "parentFolderId", folder.getParentFolderId() != null ? folder.getParentFolderId() : "root", 
                        "resourceId", id, "resourceName", request.getName()));

        return folderApplicationMapper.toDto(updatedFolder);
    }

    @Override
    @Transactional
    public FolderDto moveFolder(UUID id, MoveFolderRequest request, UUID ownerId) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException(id));

        if (!folder.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Access denied");
        }

        if (id.equals(request.getTargetParentFolderId())) {
            throw new FolderException("Cannot move folder into itself");
        }

        if (request.getTargetParentFolderId() != null) {
            // Check if target is a descendant of current folder
            List<Folder> descendants = folderRepository.findAllSubfolders(id);
            boolean isDescendant = descendants.stream()
                    .anyMatch(d -> d.getId().equals(request.getTargetParentFolderId()));

            if (isDescendant) {
                throw new FolderException("Cannot move folder into its own subfolder");
            }

            Folder targetParent = folderRepository.findById(request.getTargetParentFolderId())
                    .orElseThrow(() -> new FolderNotFoundException(request.getTargetParentFolderId()));

            if (!targetParent.getProjectId().equals(folder.getProjectId())) {
                throw new FolderException("Cannot move folder to a different project (not supported yet)");
            }
        }

        folder.move(request.getTargetParentFolderId());
        Folder movedFolder = folderRepository.save(folder);

        auditService.logActivity(ownerId, ActivityAction.FOLDER_MOVED, ResourceType.FOLDER, id, 
                Map.of("targetParentFolderId", request.getTargetParentFolderId() != null ? request.getTargetParentFolderId().toString() : "root"));

        broadcastUpdate(folder.getProjectId(), RealTimeUpdateType.FOLDER_MOVED, ownerId, 
                Map.of("projectId", folder.getProjectId(), "parentFolderId", request.getTargetParentFolderId() != null ? request.getTargetParentFolderId() : "root", 
                        "resourceId", id, "resourceName", folder.getName()));

        return folderApplicationMapper.toDto(movedFolder);
    }

    @Override
    @Transactional
    public void deleteFolder(UUID id, UUID ownerId) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException(id));

        if (!folder.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Access denied");
        }

        // Recursive soft delete for subfolders
        List<Folder> descendants = folderRepository.findAllSubfolders(id);
        descendants.add(folder); // Include the folder itself

        for (Folder f : descendants) {
            // Soft delete files in this folder
            List<File> files = fileRepository.findByFolderId(f.getId());
            for (File file : files) {
                file.softDelete();
                fileRepository.save(file);
            }

            f.softDelete();
            folderRepository.save(f);
        }
        
        auditService.logActivity(ownerId, ActivityAction.FOLDER_DELETED, ResourceType.FOLDER, id, 
                Map.of("name", folder.getName()));

        broadcastUpdate(folder.getProjectId(), RealTimeUpdateType.FOLDER_DELETED, ownerId, 
                Map.of("projectId", folder.getProjectId(), "parentFolderId", folder.getParentFolderId() != null ? folder.getParentFolderId() : "root", 
                        "resourceId", id, "resourceName", folder.getName()));
    }

    private void broadcastUpdate(UUID projectId, RealTimeUpdateType type, UUID actorId, Map<String, Object> metadata) {
        List<UUID> memberIds = shareService.getProjectMemberIds(projectId);
        memberIds.stream()
                .filter(userId -> !userId.equals(actorId))
                .forEach(userId -> realTimeUpdateService.sendSyncEvent(userId, type, metadata));
    }

    @Override
    public List<FolderDto> getFolderPath(UUID id, UUID ownerId) {
        LinkedList<FolderDto> path = new LinkedList<>();
        UUID currentId = id;

        while (currentId != null) {
            Folder folder = folderRepository.findById(currentId)
                    .orElseThrow(() -> new FolderNotFoundException(id));

            if (!folder.getOwnerId().equals(ownerId) && !shareService.hasProjectAccess(folder.getProjectId(), ownerId)) {
                throw new AccessDeniedException("Access denied");
            }

            path.addFirst(folderApplicationMapper.toDto(folder));
            currentId = folder.getParentFolderId();
        }

        return path;
    }

    @Override
    public List<FolderDto> listAllFolders(UUID projectId, UUID ownerId) {
        return folderRepository.findByProjectId(projectId).stream()
                .filter(f -> !f.isDeleted())
                .map(folderApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void downloadFolder(UUID id, UUID ownerId, OutputStream outputStream) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException(id));

        if (!folder.getOwnerId().equals(ownerId) && !shareService.hasProjectAccess(folder.getProjectId(), ownerId)) {
            throw new AccessDeniedException("You do not have access to this folder");
        }

        List<Folder> descendants = folderRepository.findAllSubfolders(id);
        List<Folder> allFolders = new ArrayList<>(descendants);
        allFolders.add(folder);

        Map<UUID, Folder> folderMap = allFolders.stream()
                .collect(Collectors.toMap(Folder::getId, f -> f));

        try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            for (Folder f : allFolders) {
                String relativePath = getRelativePath(f, id, folderMap);
                
                // Add directory entry (important for empty folders)
                if (!relativePath.isEmpty()) {
                    zipOut.putNextEntry(new ZipEntry(relativePath));
                    zipOut.closeEntry();
                }

                List<File> files = fileRepository.findByFolderId(f.getId()).stream()
                        .filter(file -> !file.isDeleted())
                        .collect(Collectors.toList());

                for (File file : files) {
                    ZipEntry zipEntry = new ZipEntry(relativePath + file.getName());
                    zipOut.putNextEntry(zipEntry);
                    
                    try (InputStream fileStream = storageService.download(file.getMinioKey())) {
                        fileStream.transferTo(zipOut);
                    }
                    zipOut.closeEntry();
                }
            }
            zipOut.finish();
        } catch (IOException e) {
            throw new FolderException("Failed to generate ZIP archive: " + e.getMessage());
        }

        auditService.logActivity(ownerId, ActivityAction.FOLDER_DOWNLOADED, ResourceType.FOLDER, id, 
                Map.of("name", folder.getName()));
    }

    private String getRelativePath(Folder folder, UUID targetId, Map<UUID, Folder> folderMap) {
        if (folder.getId().equals(targetId)) {
            return "";
        }
        
        List<String> parts = new ArrayList<>();
        Folder current = folder;
        while (current != null && !current.getId().equals(targetId)) {
            parts.add(0, current.getName());
            current = folderMap.get(current.getParentFolderId());
        }
        
        return String.join("/", parts) + "/";
    }
}
