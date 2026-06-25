package com.logopeda.audit.dto;

import java.time.Instant;

public record AuditLogResponse(
        String id,
        String clinicId,
        String userId,
        String action,
        String entityType,
        String entityId,
        String metadata,
        Instant createdAt) {
}
