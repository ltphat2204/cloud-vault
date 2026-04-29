package ltphat.cloudvault.backend.files.application.service.impl;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.files.application.dto.FileDto;
import ltphat.cloudvault.backend.files.application.dto.FileVersionDto;
import ltphat.cloudvault.backend.files.application.dto.MoveFileRequest;
import ltphat.cloudvault.backend.files.application.dto.UpdateFileRequest;
import ltphat.cloudvault.backend.files.application.mapper.FileApplicationMapper;
import ltphat.cloudvault.backend.files.application.service.IFileService;
import ltphat.cloudvault.backend.files.application.service.IStorageService;
import ltphat.cloudvault.backend.files.domain.exception.FileException;
import ltphat.cloudvault.backend.files.domain.exception.FileNotFoundException;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.model.FileVersion;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.files.domain.repository.IFileVersionRepository;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.audit.application.service.IActivityLogService;
import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import ltphat.cloudvault.backend.notifications.application.service.RealTimeUpdateService;
import ltphat.cloudvault.backend.notifications.domain.model.RealTimeUpdateType;
import ltphat.cloudvault.backend.shares.application.service.ShareService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements IFileService {

    private final IFileRepository fileRepository;
    private final IFileVersionRepository fileVersionRepository;
    private final IFolderRepository folderRepository;
    private final FileApplicationMapper fileApplicationMapper;
    private final IStorageService storageService;
    private final IActivityLogService auditService;
    private final ShareService shareService;
    private final RealTimeUpdateService realTimeUpdateService;

    @Override
    @Transactional
    public FileDto uploadFile(UUID projectId, UUID folderId, String name, String contentType, long size, java.io.InputStream inputStream, UUID ownerId) {
        // Validate folder if provided
        if (folderId != null) {
            folderRepository.findById(folderId)
                    .orElseThrow(() -> new FileException("Target folder not found"));
        }

        // Check for existing file
        Optional<File> existingFileOpt = fileRepository.findByProjectIdAndFolderId(projectId, folderId).stream()
                .filter(f -> f.getName().equals(name) && !f.isDeleted())
                .findFirst();

        File file;
        int nextVersion;

        if (existingFileOpt.isPresent()) {
            file = existingFileOpt.get();
            if (!file.getOwnerId().equals(ownerId) && !shareService.hasProjectAccess(projectId, ownerId)) {
                throw new AccessDeniedException("You do not have access to this file");
            }
            nextVersion = file.getVersionNumber() + 1;
        } else {
            file = File.builder()
                    .name(name)
                    .projectId(projectId)
                    .folderId(folderId)
                    .ownerId(ownerId)
                    .mimeType(contentType)
                    .versionNumber(0) // Will be incremented below
                    .build();
            nextVersion = 1;
        }

        // Generate MinIO key
        String minioKey = String.format("%s/%s/%s/%d_%s", ownerId, projectId, 
                folderId != null ? folderId : "root", nextVersion, name);

        // Upload to storage
        storageService.upload(minioKey, inputStream, contentType);

        // Update File entity
        file.setVersionNumber(nextVersion);
        file.setMinioKey(minioKey);
        file.setSize(size);
        file.setMimeType(contentType);
        File savedFile = fileRepository.save(file);

        // Create FileVersion record
        FileVersion version = FileVersion.builder()
                .fileId(savedFile.getId())
                .versionNumber(nextVersion)
                .minioKey(minioKey)
                .size(size)
                .build();
        FileVersion savedVersion = fileVersionRepository.save(version);

        savedFile.setCurrentVersionId(savedVersion.getId());
        fileRepository.save(savedFile);

        auditService.logActivity(ownerId, ActivityAction.FILE_UPLOADED, ResourceType.FILE, savedFile.getId(), 
                Map.of("name", name, "size", size, "version", nextVersion));

        broadcastUpdate(projectId, RealTimeUpdateType.FILE_CREATED, ownerId, 
                Map.of("projectId", projectId, "folderId", folderId != null ? folderId : "root", 
                        "resourceId", savedFile.getId(), "resourceName", name));

        return fileApplicationMapper.toDto(savedFile);
    }

    @Override
    public java.io.InputStream downloadFile(UUID fileId, Integer versionNumber, UUID ownerId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!file.getOwnerId().equals(ownerId) && !shareService.hasProjectAccess(file.getProjectId(), ownerId)) {
            throw new AccessDeniedException("You do not have access to this file");
        }

        String key;
        if (versionNumber != null) {
            FileVersion version = fileVersionRepository.findByFileIdAndVersionNumber(fileId, versionNumber)
                    .orElseThrow(() -> new FileException("Version not found"));
            key = version.getMinioKey();
        } else {
            key = file.getMinioKey();
        }

        auditService.logActivity(ownerId, ActivityAction.FILE_DOWNLOADED, ResourceType.FILE, fileId, 
                Map.of("name", file.getName(), "version", versionNumber != null ? versionNumber : file.getVersionNumber()));

        return storageService.download(key);
    }

    @Override
    public List<FileVersionDto> getFileVersions(UUID fileId, UUID ownerId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!file.getOwnerId().equals(ownerId) && !shareService.hasProjectAccess(file.getProjectId(), ownerId)) {
            throw new AccessDeniedException("You do not have access to this file");
        }

        return fileVersionRepository.findByFileId(fileId).stream()
                .map(fileApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FileDto getFile(UUID id, UUID ownerId) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(id));

        if (!file.getOwnerId().equals(ownerId) && !shareService.hasProjectAccess(file.getProjectId(), ownerId)) {
            throw new AccessDeniedException("You do not have access to this file");
        }

        if (file.isDeleted()) {
            throw new FileNotFoundException(id);
        }

        return fileApplicationMapper.toDto(file);
    }

    @Override
    public List<FileDto> listFiles(UUID projectId, UUID folderId, UUID ownerId) {
        boolean hasSharedAccess = shareService.hasProjectAccess(projectId, ownerId);
        
        return fileRepository.findByProjectIdAndFolderId(projectId, folderId).stream()
                .filter(f -> !f.isDeleted())
                .filter(f -> f.getOwnerId().equals(ownerId) || hasSharedAccess)
                .map(fileApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FileDto updateFileMetadata(UUID id, UpdateFileRequest request, UUID ownerId) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(id));

        if (!file.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Access denied");
        }

        if (fileRepository.existsByNameAndFolderIdAndProjectId(request.getName(), file.getFolderId(), file.getProjectId())) {
            throw new FileException("File with name '" + request.getName() + "' already exists in this location");
        }

        String oldName = file.getName();
        file.updateMetadata(request.getName());
        File savedFile = fileRepository.save(file);

        auditService.logActivity(ownerId, ActivityAction.FILE_RENAMED, ResourceType.FILE, id, 
                Map.of("oldName", oldName, "newName", request.getName()));

        broadcastUpdate(file.getProjectId(), RealTimeUpdateType.FILE_UPDATED, ownerId, 
                Map.of("projectId", file.getProjectId(), "folderId", file.getFolderId() != null ? file.getFolderId() : "root", 
                        "resourceId", id, "resourceName", request.getName()));

        return fileApplicationMapper.toDto(savedFile);
    }

    @Override
    @Transactional
    public FileDto moveFile(UUID id, MoveFileRequest request, UUID ownerId) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(id));

        if (!file.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Access denied");
        }

        if (request.getTargetFolderId() != null) {
            Folder targetFolder = folderRepository.findById(request.getTargetFolderId())
                    .orElseThrow(() -> new RuntimeException("Target folder not found"));
            
            if (!targetFolder.getProjectId().equals(file.getProjectId())) {
                throw new FileException("Cannot move file to a different project");
            }
        }

        if (fileRepository.existsByNameAndFolderIdAndProjectId(file.getName(), request.getTargetFolderId(), file.getProjectId())) {
            throw new FileException("File with name '" + file.getName() + "' already exists in the target location");
        }

        file.move(request.getTargetFolderId());
        File savedFile = fileRepository.save(file);

        auditService.logActivity(ownerId, ActivityAction.FILE_MOVED, ResourceType.FILE, id, 
                Map.of("targetFolderId", request.getTargetFolderId() != null ? request.getTargetFolderId().toString() : "root"));

        broadcastUpdate(file.getProjectId(), RealTimeUpdateType.FILE_MOVED, ownerId, 
                Map.of("projectId", file.getProjectId(), "folderId", request.getTargetFolderId() != null ? request.getTargetFolderId() : "root", 
                        "resourceId", id, "resourceName", file.getName()));

        return fileApplicationMapper.toDto(savedFile);
    }

    @Override
    @Transactional
    public void deleteFile(UUID id, UUID ownerId) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(id));

        if (!file.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Access denied");
        }

        file.softDelete();
        fileRepository.save(file);

        auditService.logActivity(ownerId, ActivityAction.FILE_DELETED, ResourceType.FILE, id, 
                Map.of("name", file.getName()));

        broadcastUpdate(file.getProjectId(), RealTimeUpdateType.FILE_DELETED, ownerId, 
                Map.of("projectId", file.getProjectId(), "folderId", file.getFolderId() != null ? file.getFolderId() : "root", 
                        "resourceId", id, "resourceName", file.getName()));
    }

    private void broadcastUpdate(UUID projectId, RealTimeUpdateType type, UUID actorId, Map<String, Object> metadata) {
        List<UUID> memberIds = shareService.getProjectMemberIds(projectId);
        memberIds.stream()
                .filter(userId -> !userId.equals(actorId))
                .forEach(userId -> realTimeUpdateService.sendSyncEvent(userId, type, metadata));
    }
}
