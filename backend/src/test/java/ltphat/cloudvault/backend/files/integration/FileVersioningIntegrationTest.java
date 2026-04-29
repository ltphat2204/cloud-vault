package ltphat.cloudvault.backend.files.integration;

import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.repository.IActivityLogRepository;
import ltphat.cloudvault.backend.files.application.dto.FileDto;
import ltphat.cloudvault.backend.files.application.dto.FileVersionDto;
import ltphat.cloudvault.backend.files.application.service.IFileService;
import ltphat.cloudvault.backend.shared.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class FileVersioningIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private IFileService fileService;
    
    @Autowired
    private IActivityLogRepository auditRepository;
    
    @Autowired
    private ltphat.cloudvault.backend.projects.domain.repository.IProjectRepository projectRepository;

    @Test
    void testFileVersioningFlow() throws Exception {
        UUID ownerId = UUID.randomUUID();
        
        ltphat.cloudvault.backend.projects.domain.model.Project project = projectRepository.save(
                ltphat.cloudvault.backend.projects.domain.model.Project.createNew("Test Project", ownerId));
        UUID projectId = project.getId();
        
        String fileName = "test.txt";
        
        // 1. Upload Version 1
        String content1 = "Version 1 content";
        FileDto file1 = fileService.uploadFile(
                projectId, null, fileName, "text/plain", 
                content1.length(), new ByteArrayInputStream(content1.getBytes()), ownerId
        );
        
        assertThat(file1.getVersionNumber()).isEqualTo(1);
        
        // 2. Upload Version 2
        String content2 = "Version 2 content - updated";
        FileDto file2 = fileService.uploadFile(
                projectId, null, fileName, "text/plain", 
                content2.length(), new ByteArrayInputStream(content2.getBytes()), ownerId
        );
        
        assertThat(file2.getId()).isEqualTo(file1.getId());
        assertThat(file2.getVersionNumber()).isEqualTo(2);
        
        // 3. List Versions
        List<FileVersionDto> versions = fileService.getFileVersions(file2.getId(), ownerId);
        assertThat(versions).hasSize(2);
        
        // 4. Download Version 1
        InputStream v1Stream = fileService.downloadFile(file2.getId(), 1, ownerId);
        String downloadedV1 = new String(v1Stream.readAllBytes());
        assertThat(downloadedV1).isEqualTo(content1);
        
        // 5. Download Current Version
        InputStream currentStream = fileService.downloadFile(file2.getId(), null, ownerId);
        String downloadedCurrent = new String(currentStream.readAllBytes());
        assertThat(downloadedCurrent).isEqualTo(content2);
        
        // 6. Verify Audit Logs
        var logs = auditRepository.findByUserId(ownerId, null, null, Pageable.unpaged()).getContent();
        assertThat(logs).anyMatch(l -> l.getAction().equals(ActivityAction.FILE_UPLOADED));
        assertThat(logs).anyMatch(l -> l.getAction().equals(ActivityAction.FILE_DOWNLOADED));
    }
}
