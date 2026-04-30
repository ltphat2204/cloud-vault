package ltphat.cloudvault.backend.files.application.service;

import ltphat.cloudvault.backend.files.application.dto.FileDto;
import ltphat.cloudvault.backend.files.application.dto.MoveFileRequest;
import ltphat.cloudvault.backend.files.application.dto.UpdateFileRequest;
import ltphat.cloudvault.backend.files.application.dto.FileVersionDto;

import ltphat.cloudvault.backend.shared.dto.CursorPageResponse;
import ltphat.cloudvault.backend.shared.dto.CursorParams;

import java.util.List;
import java.util.UUID;

public interface IFileService {
    FileDto getFile(UUID id, UUID ownerId);
    CursorPageResponse<FileDto> listFiles(UUID projectId, UUID folderId, UUID ownerId, CursorParams cursorParams);
    FileDto updateFileMetadata(UUID id, UpdateFileRequest request, UUID ownerId);
    FileDto moveFile(UUID id, MoveFileRequest request, UUID ownerId);
    void deleteFile(UUID id, UUID ownerId);
    
    FileDto uploadFile(UUID projectId, UUID folderId, String name, String contentType, long size, java.io.InputStream inputStream, UUID ownerId);
    java.io.InputStream downloadFile(UUID fileId, Integer versionNumber, UUID ownerId);
    List<FileVersionDto> getFileVersions(UUID fileId, UUID ownerId);
}
