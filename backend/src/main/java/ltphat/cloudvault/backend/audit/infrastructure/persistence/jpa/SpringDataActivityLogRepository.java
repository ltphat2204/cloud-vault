package ltphat.cloudvault.backend.audit.infrastructure.persistence.jpa;

import ltphat.cloudvault.backend.audit.domain.model.ActivityAction;
import ltphat.cloudvault.backend.audit.domain.model.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataActivityLogRepository extends JpaRepository<JpaActivityLog, UUID> {
    
    @Query("SELECT a FROM JpaActivityLog a WHERE a.userId = :userId " +
           "AND (:action IS NULL OR a.action = :action) " +
           "AND (:resourceType IS NULL OR a.resourceType = :resourceType)")
    Page<JpaActivityLog> findByFilters(
            @Param("userId") UUID userId, 
            @Param("action") ActivityAction action, 
            @Param("resourceType") ResourceType resourceType, 
            Pageable pageable);
            
    Page<JpaActivityLog> findByResourceIdAndResourceType(UUID resourceId, ResourceType resourceType, Pageable pageable);
}
