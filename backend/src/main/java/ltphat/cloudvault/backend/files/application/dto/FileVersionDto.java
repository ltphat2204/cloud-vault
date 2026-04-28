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
public class FileVersionDto {
    private UUID id;
    private UUID fileId;
    private Integer versionNumber;
    private Long size;
    private LocalDateTime createdAt;
}
