package ltphat.cloudvault.backend.trash.domain.repository;

import ltphat.cloudvault.backend.trash.domain.model.TrashItem;

import java.util.List;
import java.util.UUID;

public interface ITrashRepository {
    List<TrashItem> findAllDeletedByOwnerId(UUID ownerId);
}
