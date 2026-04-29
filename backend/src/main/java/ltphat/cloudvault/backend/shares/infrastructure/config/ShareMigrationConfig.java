package ltphat.cloudvault.backend.shares.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltphat.cloudvault.backend.files.domain.model.File;
import ltphat.cloudvault.backend.files.domain.repository.IFileRepository;
import ltphat.cloudvault.backend.folders.domain.model.Folder;
import ltphat.cloudvault.backend.folders.domain.repository.IFolderRepository;
import ltphat.cloudvault.backend.shares.infrastructure.persistence.jpa.JpaShare;
import ltphat.cloudvault.backend.shares.infrastructure.persistence.jpa.SpringDataShareRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ShareMigrationConfig {

    private final SpringDataShareRepository shareRepository;
    private final IFolderRepository folderRepository;
    private final IFileRepository fileRepository;

    @Bean
    public CommandLineRunner migrateShares() {
        return args -> {
            List<JpaShare> sharesToMigrate = shareRepository.findAll().stream()
                    .filter(s -> s.getProjectId() == null)
                    .toList();

            if (sharesToMigrate.isEmpty()) return;

            log.info("Migrating {} shares to include projectId...", sharesToMigrate.size());

            for (JpaShare share : sharesToMigrate) {
                try {
                    UUID projectId = switch (share.getResourceType()) {
                        case PROJECT -> share.getResourceId();
                        case FOLDER -> folderRepository.findById(share.getResourceId())
                                .map(Folder::getProjectId).orElse(null);
                        case FILE -> fileRepository.findById(share.getResourceId())
                                .map(File::getProjectId).orElse(null);
                    };

                    if (projectId != null) {
                        share.setProjectId(projectId);
                        shareRepository.save(share);
                    }
                } catch (Exception e) {
                    log.error("Failed to migrate share {}: {}", share.getId(), e.getMessage());
                }
            }

            log.info("Share migration completed.");
        };
    }
}
