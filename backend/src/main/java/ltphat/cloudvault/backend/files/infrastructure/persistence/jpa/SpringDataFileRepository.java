package ltphat.cloudvault.backend.files.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SpringDataFileRepository extends JpaRepository<JpaFile, UUID> {
    List<JpaFile> findByProjectIdAndFolderId(UUID projectId, UUID folderId, Pageable pageable);
    
    @Query("SELECT f FROM JpaFile f WHERE f.projectId = :projectId " +
           "AND (:#{#folderId == null} = true AND f.folderId IS NULL OR f.folderId = :folderId) " +
           "AND (:#{#cursorTime == null} = true OR f.createdAt < :cursorTime OR (f.createdAt = :cursorTime AND f.id < :cursorId))")
    List<JpaFile> findByProjectIdAndFolderIdWithCursor(
            @Param("projectId") UUID projectId, 
            @Param("folderId") UUID folderId, 
            @Param("cursorTime") LocalDateTime cursorTime,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);

    Optional<JpaFile> findByNameAndFolderIdAndProjectId(String name, UUID folderId, UUID projectId);
    boolean existsByNameAndFolderIdAndProjectId(String name, UUID folderId, UUID projectId);
    List<JpaFile> findByOwnerIdAndDeletedAtIsNotNull(UUID ownerId);
    List<JpaFile> findByFolderId(UUID folderId);
    List<JpaFile> findByProjectIdInAndNameContainingIgnoreCaseAndDeletedAtIsNull(List<UUID> projectIds, String query);
    List<JpaFile> findByFolderIdAndNameContainingIgnoreCaseAndDeletedAtIsNull(UUID folderId, String query);
}
