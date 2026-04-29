package ltphat.cloudvault.backend.search.application.dto;

import lombok.Builder;
import lombok.Data;
import ltphat.cloudvault.backend.search.domain.model.SearchResourceType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SearchResponse {
    private UUID id;
    private String name;
    private SearchResourceType type;
    private UUID projectId;
    private UUID parentId;
    private Long size;
    private String mimeType;
    private LocalDateTime updatedAt;
    private UUID ownerId;
}
