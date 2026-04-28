package ltphat.cloudvault.backend.folders.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataFolderRepository extends JpaRepository<JpaFolder, UUID> {
    List<JpaFolder> findByProjectIdAndParentFolderId(UUID projectId, UUID parentFolderId);
    
    boolean existsByNameAndParentFolderIdAndProjectId(String name, UUID parentFolderId, UUID projectId);

    @Query(value = "WITH RECURSIVE subfolders AS (" +
                   "  SELECT * FROM folders WHERE id = :parentId " +
                   "  UNION ALL " +
                   "  SELECT f.* FROM folders f " +
                   "  JOIN subfolders s ON f.parent_folder_id = s.id" +
                   ") SELECT * FROM subfolders WHERE id != :parentId", nativeQuery = true)
    List<JpaFolder> findAllDescendants(@Param("parentId") UUID parentId);
}
