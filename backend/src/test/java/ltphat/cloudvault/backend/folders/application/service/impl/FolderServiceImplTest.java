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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

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
    private IActivityLogService auditService;

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
        when(folderRepository.save(any(Folder.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
}
