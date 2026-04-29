package ltphat.cloudvault.backend.trash.application.service.impl;

import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.files.domain.repository.IFileVersionRepository;
import ltphat.cloudvault.backend.files.application.service.IStorageService;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import ltphat.cloudvault.backend.trash.application.mapper.TrashApplicationMapper;
import ltphat.cloudvault.backend.trash.domain.repository.ITrashRepository;
import ltphat.cloudvault.backend.audit.application.service.IActivityLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrashServiceImplTest {

    @Mock private ITrashRepository trashRepository;
    @Mock private IFileRepository fileRepository;
    @Mock private IFileVersionRepository fileVersionRepository;
    @Mock private IFolderRepository folderRepository;
    @Mock private IProjectRepository projectRepository;
    @Mock private IStorageService storageService;
    @Mock private TrashApplicationMapper trashMapper;
    @Mock private IActivityLogService auditService;

    @InjectMocks
    private TrashServiceImpl trashService;

    private UUID ownerId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        projectId = UUID.randomUUID();
    }

    @Test
    void listTrash_ShouldReturnMappedItems() {
        trashService.listTrash(ownerId);
        verify(trashRepository).findAllDeletedByOwnerId(ownerId);
        verify(trashMapper).toDtoList(any());
    }

    @Test
    void restoreItems_ShouldRestoreFile() {
        UUID fileId = UUID.randomUUID();
        File file = File.builder()
                .id(fileId)
                .name("test-file")
                .ownerId(ownerId)
                .deletedAt(LocalDateTime.now())
                .build();

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));

        trashService.restoreItems(List.of(fileId), ownerId);

        verify(fileRepository).save(argThat(f -> !f.isDeleted()));
    }

    @Test
    void restoreItems_ShouldRestoreFolderRecursively() {
        UUID folderId = UUID.randomUUID();
        Folder folder = Folder.builder()
                .id(folderId)
                .name("test-folder")
                .ownerId(ownerId)
                .projectId(projectId)
                .deletedAt(LocalDateTime.now())
                .build();

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        when(folderRepository.findByProjectIdAndParentFolderId(projectId, folderId)).thenReturn(List.of());
        when(fileRepository.findByFolderId(folderId)).thenReturn(List.of());

        trashService.restoreItems(List.of(folderId), ownerId);

        verify(folderRepository).save(argThat(f -> !f.isDeleted()));
    }

    @Test
    void deleteItemsPermanently_ShouldCallStorageAndDelete() {
        UUID fileId = UUID.randomUUID();
        File file = File.builder()
                .id(fileId)
                .name("test-file")
                .ownerId(ownerId)
                .build();

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        when(fileVersionRepository.findByFileId(fileId)).thenReturn(List.of());

        trashService.deleteItemsPermanently(List.of(fileId), ownerId);

        verify(fileRepository).hardDelete(fileId);
    }

    @Test
    void restoreItems_ShouldRestoreProject() {
        UUID projectId = UUID.randomUUID();
        Project project = Project.builder()
                .id(projectId)
                .name("test-project")
                .ownerId(ownerId)
                .deletedAt(LocalDateTime.now())
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        trashService.restoreItems(List.of(projectId), ownerId);

        verify(projectRepository).save(argThat(p -> !p.isDeleted()));
    }
}
