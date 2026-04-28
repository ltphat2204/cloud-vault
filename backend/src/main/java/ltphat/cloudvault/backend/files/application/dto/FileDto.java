package ltphat.cloudvault.backend.files.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private UUID id;
    private String name;
    private Long size;
    private String mimeType;
    private UUID folderId;
    private UUID projectId;
    private UUID ownerId;
    private Integer versionNumber;
    private UUID currentVersionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
