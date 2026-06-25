package com.logopeda.audit.model;

import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Append-only audit record for sensitive operations (patient changes, report
 * access, document downloads, consent signing, sharing with family, ...).
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_clinic", columnList = "clinicId"),
        @Index(name = "idx_audit_entity", columnList = "entityType,entityId"),
        @Index(name = "idx_audit_user", columnList = "userId")
})
public class AuditLog extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(length = 36)
    private String userId;

    @Column(nullable = false, length = 64)
    private String action;

    @Column(length = 64)
    private String entityType;

    @Column(length = 36)
    private String entityId;

    @Column(length = 2000)
    private String metadata;

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
