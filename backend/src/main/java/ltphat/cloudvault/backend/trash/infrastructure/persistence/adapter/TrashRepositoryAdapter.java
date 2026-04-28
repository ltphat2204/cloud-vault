package ltphat.cloudvault.backend.trash.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.trash.domain.model.TrashItem;
import ltphat.cloudvault.backend.trash.domain.repository.ITrashRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrashRepositoryAdapter implements ITrashRepository {

    private final IFileRepository fileRepository;
    private final IFolderRepository folderRepository;

    @Override
    public List<TrashItem> findAllDeletedByOwnerId(UUID ownerId) {
        List<File> deletedFiles = fileRepository.findAllDeletedByOwnerId(ownerId);
        List<Folder> deletedFolders = folderRepository.findAllDeletedByOwnerId(ownerId);

        List<TrashItem> trashItems = new ArrayList<>();

        trashItems.addAll(deletedFiles.stream()
                .map(file -> TrashItem.builder()
                        .id(file.getId())
                        .name(file.getName())
                        .type("FILE")
                        .size(file.getSize())
                        .deletedAt(file.getDeletedAt())
                        .projectId(file.getProjectId())
                        .build())
                .collect(Collectors.toList()));

        trashItems.addAll(deletedFolders.stream()
                .map(folder -> TrashItem.builder()
                        .id(folder.getId())
                        .name(folder.getName())
                        .type("FOLDER")
                        .size(0L) // Folders don't have a direct size in this model
                        .deletedAt(folder.getDeletedAt())
                        .projectId(folder.getProjectId())
                        .build())
                .collect(Collectors.toList()));

        return trashItems;
    }
}
