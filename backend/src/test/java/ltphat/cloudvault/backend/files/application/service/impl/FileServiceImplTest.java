package ltphat.cloudvault.backend.files.application.service.impl;

import ltphat.cloudvault.backend.files.application.dto.FileDto;
import ltphat.cloudvault.backend.files.application.dto.FileVersionDto;
import ltphat.cloudvault.backend.files.application.dto.MoveFileRequest;
import ltphat.cloudvault.backend.files.application.dto.UpdateFileRequest;
import ltphat.cloudvault.backend.files.application.mapper.FileApplicationMapper;
import ltphat.cloudvault.backend.files.application.service.IStorageService;
import ltphat.cloudvault.backend.files.domain.exception.FileException;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.model.FileVersion;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.files.domain.repository.IFileVersionRepository;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private IFileRepository fileRepository;

    @Mock
    private IFolderRepository folderRepository;

    @Mock
    private IFileVersionRepository fileVersionRepository;

    @Mock
    private IStorageService storageService;

    @Spy
    private FileApplicationMapper fileApplicationMapper;

    @InjectMocks
    private FileServiceImpl fileService;

    private UUID ownerId;
    private UUID projectId;
    private UUID fileId;
    private File file;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        fileId = UUID.randomUUID();
        file = File.builder()
                .id(fileId)
                .name("test.txt")
                .ownerId(ownerId)
                .projectId(projectId)
                .build();
    }

    @Test
    void getFile_Success() {
        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));

        FileDto result = fileService.getFile(fileId, ownerId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("test.txt");
    }

    @Test
    void getFile_AccessDenied() {
        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        UUID otherUserId = UUID.randomUUID();

        assertThatThrownBy(() -> fileService.getFile(fileId, otherUserId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void updateFileMetadata_Success() {
        UpdateFileRequest request = UpdateFileRequest.builder().name("new-name.txt").build();
        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        when(fileRepository.existsByNameAndFolderIdAndProjectId("new-name.txt", null, projectId)).thenReturn(false);
        when(fileRepository.save(any(File.class))).thenAnswer(inv -> inv.getArgument(0));

        FileDto result = fileService.updateFileMetadata(fileId, request, ownerId);

        assertThat(result.getName()).isEqualTo("new-name.txt");
        verify(fileRepository).save(any(File.class));
    }

    @Test
    void moveFile_ToDifferentProject_ThrowsException() {
        UUID targetFolderId = UUID.randomUUID();
        UUID otherProjectId = UUID.randomUUID();
        Folder targetFolder = Folder.builder().id(targetFolderId).projectId(otherProjectId).build();
        MoveFileRequest request = MoveFileRequest.builder().targetFolderId(targetFolderId).build();

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        when(folderRepository.findById(targetFolderId)).thenReturn(Optional.of(targetFolder));

        assertThatThrownBy(() -> fileService.moveFile(fileId, request, ownerId))
                .isInstanceOf(FileException.class)
                .hasMessageContaining("different project");
    }

    @Test
    void deleteFile_SoftDeletes() {
        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));

        fileService.deleteFile(fileId, ownerId);

        assertThat(file.isDeleted()).isTrue();
        verify(fileRepository).save(file);
    }

    @Test
    void uploadFile_Success() {
        // Arrange
        String name = "test.txt";
        String contentType = "text/plain";
        long size = 123L;
        java.io.InputStream inputStream = new java.io.ByteArrayInputStream("hello".getBytes());

        when(fileRepository.findByProjectIdAndFolderId(projectId, null)).thenReturn(java.util.List.of());
        when(fileRepository.save(any(File.class))).thenAnswer(inv -> {
            File f = inv.getArgument(0);
            if (f.getId() == null) {
                org.springframework.test.util.ReflectionTestUtils.setField(f, "id", fileId);
            }
            return f;
        });
        when(fileVersionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        FileDto result = fileService.uploadFile(projectId, null, name, contentType, size, inputStream, ownerId);

        // Assert
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getVersionNumber()).isEqualTo(1);
        verify(storageService).upload(anyString(), eq(inputStream), eq(contentType));
        verify(fileRepository, atLeastOnce()).save(any(File.class));
        verify(fileVersionRepository).save(any());
    }

    @Test
    void downloadFile_Success() {
        // Arrange
        java.io.InputStream inputStream = new java.io.ByteArrayInputStream("hello".getBytes());
        file.setMinioKey("some-key");
        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        when(storageService.download("some-key")).thenReturn(inputStream);

        // Act
        java.io.InputStream result = fileService.downloadFile(fileId, null, ownerId);

        // Assert
        assertThat(result).isEqualTo(inputStream);
    }

    @Test
    void getFileVersions_Success() {
        // Arrange
        FileVersion version = FileVersion.builder().id(UUID.randomUUID()).fileId(fileId).versionNumber(1).build();
        when(fileRepository.findById(fileId)).thenReturn(Optional.of(file));
        when(fileVersionRepository.findByFileId(fileId)).thenReturn(java.util.List.of(version));

        // Act
        java.util.List<FileVersionDto> result = fileService.getFileVersions(fileId, ownerId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVersionNumber()).isEqualTo(1);
    }
}
