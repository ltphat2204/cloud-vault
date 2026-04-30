package ltphat.cloudvault.backend.folders.infrastructure.persistence.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataFolderRepository extends JpaRepository<JpaFolder, UUID> {
    List<JpaFolder> findByProjectIdAndParentFolderId(UUID projectId, UUID parentFolderId, Pageable pageable);
    
    @Query("SELECT f FROM JpaFolder f WHERE f.projectId = :projectId " +
           "AND (:#{#parentFolderId == null} = true AND f.parentFolderId IS NULL OR f.parentFolderId = :parentFolderId) " +
           "AND (:#{#cursorTime == null} = true OR f.createdAt < :cursorTime OR (f.createdAt = :cursorTime AND f.id < :cursorId))")
    List<JpaFolder> findByProjectIdAndParentFolderIdWithCursor(
            @Param("projectId") UUID projectId, 
            @Param("parentFolderId") UUID parentFolderId, 
            @Param("cursorTime") LocalDateTime cursorTime,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);

    Optional<JpaFolder> findByNameAndParentFolderIdAndProjectId(String name, UUID parentFolderId, UUID projectId);
    
    List<JpaFolder> findByProjectId(UUID projectId);
    
    boolean existsByNameAndParentFolderIdAndProjectId(String name, UUID parentFolderId, UUID projectId);

    @Query(value = "WITH RECURSIVE subfolders AS (" +
                   "  SELECT * FROM folders WHERE id = :parentId " +
                   "  UNION ALL " +
                   "  SELECT f.* FROM folders f " +
                   "  JOIN subfolders s ON f.parent_folder_id = s.id" +
                   ") SELECT * FROM subfolders WHERE id != :parentId", nativeQuery = true)
    List<JpaFolder> findAllDescendants(@Param("parentId") UUID parentId);
    
    List<JpaFolder> findByOwnerIdAndDeletedAtIsNotNull(UUID ownerId);
    
    List<JpaFolder> findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(List<UUID> projectIds, String query);
    
    List<JpaFolder> findByParentFolderIdAndNameContainingIgnoreCaseAndDeletedAtIsNull(UUID parentFolderId, String query);
}
