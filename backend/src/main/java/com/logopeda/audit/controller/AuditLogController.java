package com.logopeda.audit.controller;

import com.logopeda.audit.dto.AuditLogResponse;
import com.logopeda.audit.model.AuditLog;
import com.logopeda.audit.repository.AuditLogRepository;
import com.logopeda.shared.security.TenantContext;
import java.time.Instant;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Read-only audit log access for clinic admins. */
@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasRole('CLINIC_ADMIN')")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;
    private final TenantContext tenantContext;

    public AuditLogController(AuditLogRepository auditLogRepository, TenantContext tenantContext) {
        this.auditLogRepository = auditLogRepository;
        this.tenantContext = tenantContext;
    }

    @GetMapping
    public List<AuditLogResponse> list(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        String clinicId = tenantContext.requireClinicId();
        return auditLogRepository.search(clinicId, entityType, entityId, userId, from, to)
                .stream().map(this::toResponse).toList();
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getClinicId(),
                log.getUserId(),
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getMetadata(),
                log.getCreatedAt());
    }
}
