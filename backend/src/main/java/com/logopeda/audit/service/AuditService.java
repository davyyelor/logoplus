package com.logopeda.audit.service;

import com.logopeda.audit.model.AuditLog;
import com.logopeda.audit.repository.AuditLogRepository;
import com.logopeda.shared.security.AuthenticatedUser;
import com.logopeda.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Records audit entries for sensitive operations. Writes run in their own
 * transaction so an audit failure never rolls back the business operation.
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final TenantContext tenantContext;

    public AuditService(AuditLogRepository auditLogRepository, TenantContext tenantContext) {
        this.auditLogRepository = auditLogRepository;
        this.tenantContext = tenantContext;
    }

    /** Records an action using the current tenant/user from the security context. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String action, String entityType, String entityId, String metadata) {
        AuthenticatedUser user = tenantContext.currentUserOrNull();
        String clinicId = user != null && user.getClinicId() != null
                ? user.getClinicId()
                : TenantContext.DEMO_CLINIC_ID;
        String userId = user != null ? user.getUserId() : null;
        persist(clinicId, userId, action, entityType, entityId, metadata);
    }

    /** Records an action with an explicit clinic/user (e.g. background jobs). */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String clinicId, String userId, String action, String entityType,
                       String entityId, String metadata) {
        persist(clinicId, userId, action, entityType, entityId, metadata);
    }

    private void persist(String clinicId, String userId, String action, String entityType,
                         String entityId, String metadata) {
        AuditLog log = new AuditLog();
        log.setClinicId(clinicId);
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setMetadata(metadata);
        auditLogRepository.save(log);
    }
}
