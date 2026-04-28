package ltphat.cloudvault.backend.trash.application.service.impl;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.model.FileVersion;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.files.domain.repository.IFileVersionRepository;
import ltphat.cloudvault.backend.files.application.service.IStorageService;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.trash.application.dto.TrashItemDto;
import ltphat.cloudvault.backend.trash.application.mapper.TrashApplicationMapper;
import ltphat.cloudvault.backend.trash.application.service.ITrashService;
import ltphat.cloudvault.backend.trash.domain.model.TrashItem;
import ltphat.cloudvault.backend.trash.domain.repository.ITrashRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrashServiceImpl implements ITrashService {

    private final ITrashRepository trashRepository;
    private final IFileRepository fileRepository;
    private final IFileVersionRepository fileVersionRepository;
    private final IFolderRepository folderRepository;
    private final IStorageService storageService;
    private final TrashApplicationMapper trashMapper;

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
            });

            // Try as folder
            folderRepository.findById(id).ifPresent(folder -> {
                if (!folder.getOwnerId().equals(ownerId)) throw new AccessDeniedException("Access denied");
                restoreFolderRecursive(folder);
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
            });

            folderRepository.findById(id).ifPresent(folder -> {
                if (!folder.getOwnerId().equals(ownerId)) throw new AccessDeniedException("Access denied");
                hardDeleteFolderRecursive(folder);
            });
        }
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
    }

    @Override
    @Transactional
    public void recoverAll(UUID ownerId) {
        List<TrashItem> items = trashRepository.findAllDeletedByOwnerId(ownerId);
        List<UUID> ids = items.stream().map(TrashItem::getId).collect(Collectors.toList());
        restoreItems(ids, ownerId);
    }
}
