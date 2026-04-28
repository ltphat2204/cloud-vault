package ltphat.cloudvault.backend.trash.domain.model;

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
public class TrashItem {
    private UUID id;
    private String name;
    private String type; // FILE or FOLDER
    private Long size;
    private LocalDateTime deletedAt;
    private UUID projectId;
    private String originalPath;
}
