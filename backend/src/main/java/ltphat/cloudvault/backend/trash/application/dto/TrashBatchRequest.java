package ltphat.cloudvault.backend.trash.application.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class TrashBatchRequest {
    private List<UUID> itemIds;
}
