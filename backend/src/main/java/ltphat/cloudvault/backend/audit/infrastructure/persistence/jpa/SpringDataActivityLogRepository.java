package ltphat.cloudvault.backend.audit.infrastructure.persistence.jpa;

import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataActivityLogRepository extends JpaRepository<JpaActivityLog, UUID> {
    
    @Query("SELECT a FROM JpaActivityLog a WHERE a.userId = :userId " +
           "AND (:#{#action == null} = true OR a.action = :action) " +
           "AND (:#{#resourceType == null} = true OR a.resourceType = :resourceType) " +
           "AND (:#{#cursorTime == null} = true OR a.createdAt < :cursorTime OR (a.createdAt = :cursorTime AND a.id < :cursorId))")
    List<JpaActivityLog> findByFiltersWithCursor(
            @Param("userId") UUID userId, 
            @Param("action") ActivityAction action, 
            @Param("resourceType") ResourceType resourceType, 
            @Param("cursorTime") LocalDateTime cursorTime,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);
            
    @Query("SELECT a FROM JpaActivityLog a WHERE a.resourceId = :resourceId AND a.resourceType = :resourceType " +
           "AND (:#{#cursorTime == null} = true OR a.createdAt < :cursorTime OR (a.createdAt = :cursorTime AND a.id < :cursorId))")
    List<JpaActivityLog> findByResourceIdAndResourceTypeWithCursor(
            @Param("resourceId") UUID resourceId, 
            @Param("resourceType") ResourceType resourceType, 
            @Param("cursorTime") LocalDateTime cursorTime,
            @Param("cursorId") UUID cursorId,
            Pageable pageable);
}
