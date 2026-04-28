package ltphat.cloudvault.backend.files.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileVersion {
    private UUID id;
    private UUID fileId;
    private Integer versionNumber;
    private String minioKey;
    private Long size;
    private LocalDateTime createdAt;

    public static FileVersion create(UUID fileId, Integer versionNumber, String minioKey, Long size) {
        return FileVersion.builder()
                .fileId(fileId)
                .versionNumber(versionNumber)
                .minioKey(minioKey)
                .size(size)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
