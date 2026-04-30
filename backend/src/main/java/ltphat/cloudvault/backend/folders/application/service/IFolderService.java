package ltphat.cloudvault.backend.folders.application.service;

import ltphat.cloudvault.backend.folders.application.dto.CreateFolderRequest;
import ltphat.cloudvault.backend.folders.application.dto.FolderDto;
import ltphat.cloudvault.backend.folders.application.dto.MoveFolderRequest;
import ltphat.cloudvault.backend.folders.application.dto.UpdateFolderRequest;

import ltphat.cloudvault.backend.shared.dto.CursorPageResponse;
import ltphat.cloudvault.backend.shared.dto.CursorParams;

import java.util.List;
import java.util.UUID;

public interface IFolderService {
    FolderDto createFolder(CreateFolderRequest request, UUID ownerId);
    FolderDto getFolder(UUID id, UUID ownerId);
    CursorPageResponse<FolderDto> listFolders(UUID projectId, UUID parentFolderId, UUID ownerId, CursorParams cursorParams);
    FolderDto updateFolder(UUID id, UpdateFolderRequest request, UUID ownerId);
    FolderDto moveFolder(UUID id, MoveFolderRequest request, UUID ownerId);
    void deleteFolder(UUID id, UUID ownerId);
    List<FolderDto> getFolderPath(UUID id, UUID ownerId);
    List<FolderDto> listAllFolders(UUID projectId, UUID ownerId);
    void downloadFolder(UUID id, UUID ownerId, java.io.OutputStream outputStream);
}
