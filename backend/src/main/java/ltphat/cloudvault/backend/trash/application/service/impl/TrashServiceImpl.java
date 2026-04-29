package ltphat.cloudvault.backend.trash.application.service.impl;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.model.FileVersion;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.files.domain.repository.IFileVersionRepository;
import ltphat.cloudvault.backend.files.application.service.IStorageService;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import ltphat.cloudvault.backend.trash.application.dto.TrashItemDto;
import ltphat.cloudvault.backend.trash.application.mapper.TrashApplicationMapper;
import ltphat.cloudvault.backend.trash.application.service.ITrashService;
import ltphat.cloudvault.backend.trash.domain.model.TrashItem;
import ltphat.cloudvault.backend.trash.domain.repository.ITrashRepository;
import ltphat.cloudvault.backend.audit.application.service.IActivityLogService;
import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrashServiceImpl implements ITrashService {

    private final ITrashRepository trashRepository;
    private final IFileRepository fileRepository;
    private final IFileVersionRepository fileVersionRepository;
    private final IFolderRepository folderRepository;
    private final IProjectRepository projectRepository;
    private final IStorageService storageService;
    private final TrashApplicationMapper trashMapper;
    private final IActivityLogService auditService;

    @Override
    public List<TrashItemDto> listTrash(UUID ownerId) {
        List<TrashItem> items = trashRepository.findAllDeletedByOwnerId(ownerId);
        return trashMapper.toDtoList(items);
    }

    @Override
    @Transactional
    public void restoreItems(List<UUID> itemIds, UUID ownerId) {
        for (UUID id : itemIds) {
            // Try as file
            fileRepository.findById(id).ifPresent(file -> {
                if (!file.getOwnerId().equals(ownerId)) throw new AccessDeniedException("Access denied");
                restoreFile(file);
                auditService.logActivity(ownerId, ActivityAction.FILE_RESTORED, ResourceType.FILE, id, 
                        Map.of("name", file.getName()));
            });

            // Try as folder
            folderRepository.findById(id).ifPresent(folder -> {
                if (!folder.getOwnerId().equals(ownerId)) throw new AccessDeniedException("Access denied");
                restoreFolderRecursive(folder);
                auditService.logActivity(ownerId, ActivityAction.FOLDER_RESTORED, ResourceType.FOLDER, id, 
                        Map.of("name", folder.getName()));
            });

            // Try as project
            projectRepository.findById(id).ifPresent(project -> {
                if (!project.getOwnerId().equals(ownerId)) throw new AccessDeniedException("Access denied");
                project.restore();
                projectRepository.save(project);
                auditService.logActivity(ownerId, ActivityAction.PROJECT_RESTORED, ResourceType.PROJECT, id, 
                        Map.of("name", project.getName()));
            });
        }
    }

    private void restoreFile(File file) {
        if (file.getFolderId() != null) {
            folderRepository.findById(file.getFolderId()).ifPresentOrElse(parent -> {
                if (parent.isDeleted()) {
                    file.move(null); // Parent is deleted, move to root
                }
            }, () -> file.move(null)); // Parent doesn't exist, move to root
        }
        
        // Manual implementation of clearing deletedAt as the model might not have a public setter for it 
        // but it has softDelete(). I'll need to add a restore() method to the model or use a workaround.
        // Looking at File.java, it only has softDelete().
        // I'll add restore() to File and Folder domain models.
        file.restore(); 
        fileRepository.save(file);
    }

    private void restoreFolderRecursive(Folder folder) {
        if (folder.getParentFolderId() != null) {
            folderRepository.findById(folder.getParentFolderId()).ifPresentOrElse(parent -> {
                if (parent.isDeleted()) {
                    folder.move(null);
                }
            }, () -> folder.move(null));
        }

        folder.restore();
        folderRepository.save(folder);

        // Recursively restore subfolders and files that were deleted in the same timeframe
        // For simplicity, we restore all files/folders directly under it that are marked as deleted
        // In a real app, we might want to be more specific about "restored with folder"
        
        List<Folder> subfolders = folderRepository.findByProjectIdAndParentFolderId(folder.getProjectId(), folder.getId());
        for (Folder sub : subfolders) {
            if (sub.isDeleted()) restoreFolderRecursive(sub);
        }

        List<File> files = fileRepository.findByFolderId(folder.getId());
        for (File file : files) {
            if (file.isDeleted()) {
                file.restore();
                fileRepository.save(file);
            }
        }
    }

    @Override
    @Transactional
    public void deleteItemsPermanently(List<UUID> itemIds, UUID ownerId) {
        for (UUID id : itemIds) {
            fileRepository.findById(id).ifPresent(file -> {
                if (!file.getOwnerId().equals(ownerId)) throw new AccessDeniedException("Access denied");
                hardDeleteFile(file);
                auditService.logActivity(ownerId, ActivityAction.FILE_PERMANENTLY_DELETED, ResourceType.FILE, id, 
                        Map.of("name", file.getName()));
            });

            folderRepository.findById(id).ifPresent(folder -> {
                if (!folder.getOwnerId().equals(ownerId)) throw new AccessDeniedException("Access denied");
                hardDeleteFolderRecursive(folder);
                auditService.logActivity(ownerId, ActivityAction.FOLDER_PERMANENTLY_DELETED, ResourceType.FOLDER, id, 
                        Map.of("name", folder.getName()));
            });

            // Try as project
            projectRepository.findById(id).ifPresent(project -> {
                if (!project.getOwnerId().equals(ownerId)) throw new AccessDeniedException("Access denied");
                hardDeleteProjectRecursive(project);
                auditService.logActivity(ownerId, ActivityAction.PROJECT_PERMANENTLY_DELETED, ResourceType.PROJECT, id, 
                        Map.of("name", project.getName()));
            });
        }
    }

    private void hardDeleteProjectRecursive(Project project) {
        // Delete all folders (which recursively deletes files)
        List<Folder> folders = folderRepository.findByProjectId(project.getId());
        for (Folder folder : folders) {
            // Delete all folders that have no parent (the root folders)
            if (folder.getParentFolderId() == null) {
                hardDeleteFolderRecursive(folder);
            }
        }
        
        // Delete project
        projectRepository.deleteById(project.getId());
    }

    private void hardDeleteFile(File file) {
        // Delete all versions from MinIO
        List<FileVersion> versions = fileVersionRepository.findByFileId(file.getId());
        for (FileVersion v : versions) {
            storageService.delete(v.getMinioKey());
            fileVersionRepository.deleteById(v.getId());
        }
        fileRepository.hardDelete(file.getId());
    }

    private void hardDeleteFolderRecursive(Folder folder) {
        // Delete files in this folder
        List<File> files = fileRepository.findByFolderId(folder.getId());
        for (File file : files) {
            hardDeleteFile(file);
        }

        // Delete subfolders
        List<Folder> subfolders = folderRepository.findByProjectIdAndParentFolderId(folder.getProjectId(), folder.getId());
        for (Folder sub : subfolders) {
            hardDeleteFolderRecursive(sub);
        }

        folderRepository.hardDelete(folder.getId());
    }

    @Override
    @Transactional
    public void emptyTrash(UUID ownerId) {
        List<TrashItem> items = trashRepository.findAllDeletedByOwnerId(ownerId);
        List<UUID> ids = items.stream().map(TrashItem::getId).collect(Collectors.toList());
        deleteItemsPermanently(ids, ownerId);
        auditService.logActivity(ownerId, ActivityAction.TRASH_EMPTIED, null, null, Map.of("count", ids.size()));
    }

    @Override
    @Transactional
    public void recoverAll(UUID ownerId) {
        List<TrashItem> items = trashRepository.findAllDeletedByOwnerId(ownerId);
        List<UUID> ids = items.stream().map(TrashItem::getId).collect(Collectors.toList());
        restoreItems(ids, ownerId);
        auditService.logActivity(ownerId, ActivityAction.TRASH_RECOVERED, null, null, Map.of("count", ids.size()));
    }
}
