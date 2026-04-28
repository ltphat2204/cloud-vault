package ltphat.cloudvault.backend.folders.application.service;

import ltphat.cloudvault.backend.folders.application.dto.CreateFolderRequest;
import ltphat.cloudvault.backend.folders.application.dto.FolderDto;
import ltphat.cloudvault.backend.folders.application.dto.MoveFolderRequest;
import ltphat.cloudvault.backend.folders.application.dto.UpdateFolderRequest;

import java.util.List;
import java.util.UUID;

public interface IFolderService {
    FolderDto createFolder(CreateFolderRequest request, UUID ownerId);
    FolderDto getFolder(UUID id, UUID ownerId);
    List<FolderDto> listFolders(UUID projectId, UUID parentFolderId, UUID ownerId);
    FolderDto updateFolder(UUID id, UpdateFolderRequest request, UUID ownerId);
    FolderDto moveFolder(UUID id, MoveFolderRequest request, UUID ownerId);
    void deleteFolder(UUID id, UUID ownerId);
}
