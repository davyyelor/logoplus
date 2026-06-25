package com.logopeda.audit.repository;

import com.logopeda.audit.model.AuditLog;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    @Query("""
            SELECT a FROM AuditLog a
            WHERE a.clinicId = :clinicId
              AND (:entityType IS NULL OR a.entityType = :entityType)
              AND (:entityId IS NULL OR a.entityId = :entityId)
              AND (:userId IS NULL OR a.userId = :userId)
              AND (:from IS NULL OR a.createdAt >= :from)
              AND (:to IS NULL OR a.createdAt <= :to)
            ORDER BY a.createdAt DESC
            """)
    List<AuditLog> search(@Param("clinicId") String clinicId,
                         @Param("entityType") String entityType,
                         @Param("entityId") String entityId,
                         @Param("userId") String userId,
                         @Param("from") Instant from,
                         @Param("to") Instant to);
}
