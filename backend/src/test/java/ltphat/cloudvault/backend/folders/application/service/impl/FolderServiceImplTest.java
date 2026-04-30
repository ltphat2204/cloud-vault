package ltphat.cloudvault.backend.folders.application.service.impl;

import ltphat.cloudvault.backend.folders.application.dto.CreateFolderRequest;
import ltphat.cloudvault.backend.folders.application.dto.FolderDto;
import ltphat.cloudvault.backend.folders.application.dto.MoveFolderRequest;
import ltphat.cloudvault.backend.folders.application.mapper.FolderApplicationMapper;
import ltphat.cloudvault.backend.folders.domain.exception.FolderException;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import ltphat.cloudvault.backend.audit.application.service.IActivityLogService;
import ltphat.cloudvault.backend.files.application.service.IStorageService;
import ltphat.cloudvault.backend.notifications.application.service.RealTimeUpdateService;
import ltphat.cloudvault.backend.shares.application.service.ShareService;
import ltphat.cloudvault.backend.shared.dto.CursorPageResponse;
import ltphat.cloudvault.backend.shared.dto.CursorParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FolderServiceImplTest {

    @Mock
    private IFolderRepository folderRepository;

    @Mock
    private IProjectRepository projectRepository;

    @Mock
    private IFileRepository fileRepository;

    @Spy
    private FolderApplicationMapper folderApplicationMapper;

    @Mock
    private IStorageService storageService;

    @Mock
    private IActivityLogService auditService;
    
    @Mock
    private ShareService shareService;

    @Mock
    private RealTimeUpdateService realTimeUpdateService;

    @InjectMocks
    private FolderServiceImpl folderService;

    private UUID ownerId;
    private UUID projectId;
    private Project project;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        project = Project.builder()
                .id(projectId)
                .ownerId(ownerId)
                .build();
        
        lenient().when(shareService.getProjectMemberIds(any())).thenReturn(java.util.List.of());
    }

    @Test
    void createFolder_Success() {
        // Arrange
        CreateFolderRequest request = CreateFolderRequest.builder()
                .name("New Folder")
                .projectId(projectId)
                .parentFolderId(null)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(folderRepository.existsByNameAndParentFolderIdAndProjectId(anyString(), any(), any())).thenReturn(false);
        when(folderRepository.save(any(Folder.class))).thenAnswer(invocation -> {
            Folder f = invocation.getArgument(0);
            return Folder.builder()
                    .id(UUID.randomUUID())
                    .name(f.getName())
                    .parentFolderId(f.getParentFolderId())
                    .projectId(f.getProjectId())
                    .ownerId(f.getOwnerId())
                    .build();
        });

        // Act
        FolderDto result = folderService.createFolder(request, ownerId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Folder");
        verify(folderRepository).save(any(Folder.class));
    }

    @Test
    void createFolder_DuplicateName_ThrowsException() {
        // Arrange
        CreateFolderRequest request = CreateFolderRequest.builder()
                .name("Existing")
                .projectId(projectId)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(folderRepository.existsByNameAndParentFolderIdAndProjectId("Existing", null, projectId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> folderService.createFolder(request, ownerId))
                .isInstanceOf(FolderException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void moveFolder_CircularReference_ThrowsException() {
        // Arrange
        UUID folderId = UUID.randomUUID();
        UUID childId = UUID.randomUUID();
        Folder folder = Folder.builder().id(folderId).ownerId(ownerId).projectId(projectId).build();
        
        MoveFolderRequest request = MoveFolderRequest.builder().targetParentFolderId(childId).build();

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        // Simulate childId being a descendant of folderId
        when(folderRepository.findAllSubfolders(folderId)).thenReturn(new java.util.ArrayList<>(java.util.List.of(Folder.builder().id(childId).build())));

        // Act & Assert
        assertThatThrownBy(() -> folderService.moveFolder(folderId, request, ownerId))
                .isInstanceOf(FolderException.class)
                .hasMessageContaining("Cannot move folder into its own subfolder");
    }

    @Test
    void deleteFolder_SoftDeletesRecursive() {
        // Arrange
        UUID folderId = UUID.randomUUID();
        Folder folder = Folder.builder().id(folderId).name("test").ownerId(ownerId).projectId(projectId).build();
        Folder subfolder = Folder.builder().id(UUID.randomUUID()).name("sub").ownerId(ownerId).projectId(projectId).build();

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        when(folderRepository.findAllSubfolders(folderId)).thenReturn(new java.util.ArrayList<>(java.util.List.of(subfolder)));
        when(fileRepository.findByFolderId(any())).thenReturn(java.util.List.of());

        // Act
        folderService.deleteFolder(folderId, ownerId);

        // Assert
        verify(folderRepository, times(2)).save(any(Folder.class));
        assertThat(folder.isDeleted()).isTrue();
        assertThat(subfolder.isDeleted()).isTrue();
    }

    @Test
    void getFolderPath_Success() {
        // Arrange
        UUID parentId = UUID.randomUUID();
        UUID folderId = UUID.randomUUID();
        Folder parent = Folder.builder().id(parentId).name("Parent").ownerId(ownerId).projectId(projectId).build();
        Folder folder = Folder.builder().id(folderId).name("Child").parentFolderId(parentId).ownerId(ownerId).projectId(projectId).build();

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        when(folderRepository.findById(parentId)).thenReturn(Optional.of(parent));

        // Act
        java.util.List<FolderDto> result = folderService.getFolderPath(folderId, ownerId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Parent");
        assertThat(result.get(1).getName()).isEqualTo("Child");
    }

    @Test
    void listAllFolders_Success() {
        // Arrange
        Folder f1 = Folder.builder().id(UUID.randomUUID()).name("F1").ownerId(ownerId).projectId(projectId).build();
        Folder f2 = Folder.builder().id(UUID.randomUUID()).name("F2").ownerId(ownerId).projectId(projectId).build();

        when(folderRepository.findByProjectId(projectId)).thenReturn(java.util.List.of(f1, f2));

        // Act
        java.util.List<FolderDto> result = folderService.listAllFolders(projectId, ownerId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.stream().map(FolderDto::getName).toList()).containsExactlyInAnyOrder("F1", "F2");
    }

    @Test
    void listFolders_SharedAccess_Success() {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        Folder f1 = Folder.builder().id(UUID.randomUUID()).name("Shared F1").ownerId(ownerId).projectId(projectId).build();
        CursorParams params = CursorParams.builder().build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(shareService.hasProjectAccess(projectId, otherUserId)).thenReturn(true);
        when(folderRepository.findByProjectIdAndParentFolderId(eq(projectId), eq(null), any(CursorParams.class))).thenReturn(java.util.List.of(f1));

        // Act
        CursorPageResponse<FolderDto> result = folderService.listFolders(projectId, null, otherUserId, params);

        // Assert
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Shared F1");
        verify(shareService).hasProjectAccess(projectId, otherUserId);
    }

    @Test
    void listFolders_AccessDenied() {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        CursorParams params = CursorParams.builder().build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(shareService.hasProjectAccess(projectId, otherUserId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> folderService.listFolders(projectId, null, otherUserId, params))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }

    @Test
    void downloadFolder_Success() throws Exception {
        // Arrange
        UUID folderId = UUID.randomUUID();
        Folder folder = Folder.builder().id(folderId).name("Root").ownerId(ownerId).projectId(projectId).build();
        
        UUID subFolderId = UUID.randomUUID();
        Folder subfolder = Folder.builder().id(subFolderId).name("Sub").parentFolderId(folderId).ownerId(ownerId).projectId(projectId).build();
        
        ltphat.cloudvault.backend.files.domain.model.File file = ltphat.cloudvault.backend.files.domain.model.File.builder()
                .id(UUID.randomUUID())
                .name("test.txt")
                .folderId(subFolderId)
                .minioKey("key1")
                .build();

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        when(folderRepository.findAllSubfolders(folderId)).thenReturn(new ArrayList<>(java.util.List.of(subfolder)));
        when(fileRepository.findByFolderId(folderId)).thenReturn(new ArrayList<>());
        when(fileRepository.findByFolderId(subFolderId)).thenReturn(new ArrayList<>(java.util.List.of(file)));
        when(storageService.download("key1")).thenReturn(new ByteArrayInputStream("hello".getBytes()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Act
        folderService.downloadFolder(folderId, ownerId, outputStream);

        // Assert
        byte[] zipBytes = outputStream.toByteArray();
        assertThat(zipBytes).isNotEmpty();

        try (ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            java.util.zip.ZipEntry entry = zipIn.getNextEntry();
            assertThat(entry).isNotNull();
            assertThat(entry.getName()).isEqualTo("Sub/");
            
            entry = zipIn.getNextEntry();
            assertThat(entry).isNotNull();
            assertThat(entry.getName()).isEqualTo("Sub/test.txt");
            
            byte[] content = zipIn.readAllBytes();
            assertThat(new String(content)).isEqualTo("hello");
        }
        
        verify(auditService).logActivity(eq(ownerId), eq(ltphat.cloudvault.backend.audit.domain.model.ActivityAction.FOLDER_DOWNLOADED), any(), eq(folderId), any());
    }
}
