package ltphat.cloudvault.backend.trash.integration;

import ltphat.cloudvault.backend.files.application.service.IFileService;
import ltphat.cloudvault.backend.folders.application.dto.CreateFolderRequest;
import ltphat.cloudvault.backend.folders.application.dto.FolderDto;
import ltphat.cloudvault.backend.folders.application.service.IFolderService;
import ltphat.cloudvault.backend.projects.domain.model.Project;
import ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository;
import ltphat.cloudvault.backend.shared.AbstractIntegrationTest;
import ltphat.cloudvault.backend.trash.application.dto.TrashItemDto;
import ltphat.cloudvault.backend.trash.application.service.ITrashService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class TrashIntegrationTest extends AbstractIntegrationTest {

    @Autowired private IFileService fileService;
    @Autowired private IFolderService folderService;
    @Autowired private ITrashService trashService;
    @Autowired private IProjectRepository projectRepository;

    @Test
    void testTrashManagementFlow() throws Exception {
        UUID ownerId = UUID.randomUUID();
        Project project = projectRepository.save(Project.createNew("Test Project", ownerId));
        UUID projectId = project.getId();

        // 1. Create a folder and a file
        CreateFolderRequest folderReq = CreateFolderRequest.builder()
                .name("Parent Folder")
                .projectId(projectId)
                .build();
        FolderDto parentFolder = folderService.createFolder(folderReq, ownerId);

        String content = "Internal File";
        fileService.uploadFile(
                projectId, parentFolder.getId(), "secret.txt", "text/plain",
                content.length(), new ByteArrayInputStream(content.getBytes()), ownerId
        );

        // 2. Soft delete the folder
        folderService.deleteFolder(parentFolder.getId(), ownerId);

        // 3. Verify items in trash
        List<TrashItemDto> trashItems = trashService.listTrash(ownerId);
        assertThat(trashItems).hasSize(2); // Folder + File
        
        // 4. Restore the folder
        trashService.restoreItems(List.of(parentFolder.getId()), ownerId);

        // 5. Verify items are restored
        trashItems = trashService.listTrash(ownerId);
        assertThat(trashItems).isEmpty();

        // 6. Permanently delete everything (empty trash)
        folderService.deleteFolder(parentFolder.getId(), ownerId);
        trashService.emptyTrash(ownerId);
        
        trashItems = trashService.listTrash(ownerId);
        assertThat(trashItems).isEmpty();
    }

    @Test
    void testProjectRestorationFlow() {
        UUID ownerId = UUID.randomUUID();
        Project project = projectRepository.save(Project.createNew("Restorable Project", ownerId));
        UUID projectId = project.getId();

        // 1. Soft delete the project
        project.softDelete();
        projectRepository.save(project);

        // 2. Verify project in trash
        List<TrashItemDto> trashItems = trashService.listTrash(ownerId);
        assertThat(trashItems).anyMatch(item -> item.getId().equals(projectId) && item.getType().equals("PROJECT"));

        // 3. Restore the project
        trashService.restoreItems(List.of(projectId), ownerId);

        // 4. Verify project is restored
        Project restored = projectRepository.findById(projectId).orElseThrow();
        assertThat(restored.isDeleted()).isFalse();
        
        trashItems = trashService.listTrash(ownerId);
        assertThat(trashItems.stream().noneMatch(item -> item.getId().equals(projectId))).isTrue();
    }
}
