package ltphat.cloudvault.backend.folders.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFolderRequest {
    private String name;
    private UUID parentFolderId;
    private UUID projectId;
}
