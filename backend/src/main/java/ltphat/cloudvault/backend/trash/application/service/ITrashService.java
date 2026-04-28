package ltphat.cloudvault.backend.trash.application.service;

import ltphat.cloudvault.backend.trash.application.dto.TrashItemDto;
import java.util.List;
import java.util.UUID;

public interface ITrashService {
    List<TrashItemDto> listTrash(UUID ownerId);
    void restoreItems(List<UUID> itemIds, UUID ownerId);
    void deleteItemsPermanently(List<UUID> itemIds, UUID ownerId);
    void emptyTrash(UUID ownerId);
    void recoverAll(UUID ownerId);
}
