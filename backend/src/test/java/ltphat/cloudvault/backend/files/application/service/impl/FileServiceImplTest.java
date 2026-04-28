package ltphat.cloudvault.backend.files.application.service.impl;

import ltphat.cloudvault.backend.files.application.dto.FileDto;
import ltphat.cloudvault.backend.files.application.dto.MoveFileRequest;
import ltphat.cloudvault.backend.files.application.dto.UpdateFileRequest;
import ltphat.cloudvault.backend.files.application.mapper.FileApplicationMapper;
import ltphat.cloudvault.backend.files.domain.exception.FileException;
import ltphat.cloudvault.backend.files.domain.exception.FileNotFoundException;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
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
}
