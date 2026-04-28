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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements IFolderService {

    private final IFolderRepository folderRepository;
    private final IFileRepository fileRepository;
    private final IProjectRepository projectRepository;
    private final FolderApplicationMapper folderApplicationMapper;

    @Override
    @Transactional
    public FolderDto createFolder(CreateFolderRequest request, UUID ownerId) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(ownerId)) {
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

        return folderApplicationMapper.toDto(savedFolder);
    }

    @Override
    public FolderDto getFolder(UUID id, UUID ownerId) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException(id));

        if (!folder.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have access to this folder");
        }

        if (folder.isDeleted()) {
            throw new FolderNotFoundException(id);
        }

        return folderApplicationMapper.toDto(folder);
    }

    @Override
    public List<FolderDto> listFolders(UUID projectId, UUID parentFolderId, UUID ownerId) {
        // Basic security check could be added here if needed to verify project access
        return folderRepository.findByProjectIdAndParentFolderId(projectId, parentFolderId).stream()
                .filter(f -> !f.isDeleted())
                .map(folderApplicationMapper::toDto)
                .collect(Collectors.toList());
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

        folder.update(request.getName());
        Folder updatedFolder = folderRepository.save(folder);

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
    }

    @Override
    public List<FolderDto> getFolderPath(UUID id, UUID ownerId) {
        LinkedList<FolderDto> path = new LinkedList<>();
        UUID currentId = id;

        while (currentId != null) {
            Folder folder = folderRepository.findById(currentId)
                    .orElseThrow(() -> new FolderNotFoundException(id));

            if (!folder.getOwnerId().equals(ownerId)) {
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
}
