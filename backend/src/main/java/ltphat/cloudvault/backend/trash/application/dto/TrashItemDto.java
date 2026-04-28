package ltphat.cloudvault.backend.trash.application.dto;

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
public class TrashItemDto {
    private UUID id;
    private String name;
    private String type;
    private Long size;
    private LocalDateTime deletedAt;
    private UUID projectId;
    private String originalPath;
}
