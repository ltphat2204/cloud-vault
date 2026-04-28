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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
            if (!file.getOwnerId().equals(ownerId)) {
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

        return fileApplicationMapper.toDto(savedFile);
    }

    @Override
    public java.io.InputStream downloadFile(UUID fileId, Integer versionNumber, UUID ownerId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!file.getOwnerId().equals(ownerId)) {
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

        return storageService.download(key);
    }

    @Override
    public List<FileVersionDto> getFileVersions(UUID fileId, UUID ownerId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!file.getOwnerId().equals(ownerId)) {
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

        if (!file.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have access to this file");
        }

        if (file.isDeleted()) {
            throw new FileNotFoundException(id);
        }

        return fileApplicationMapper.toDto(file);
    }

    @Override
    public List<FileDto> listFiles(UUID projectId, UUID folderId, UUID ownerId) {
        // In a real scenario, we'd verify project/folder access here
        return fileRepository.findByProjectIdAndFolderId(projectId, folderId).stream()
                .filter(f -> !f.isDeleted())
                .filter(f -> f.getOwnerId().equals(ownerId)) // Basic security
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

        file.updateMetadata(request.getName());
        File savedFile = fileRepository.save(file);

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
    }
}
