package com.logopeda.reminder.model;

import com.logopeda.reminder.enums.ReminderStatus;
import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * An internal reminder scheduled for a future moment. Delivery is performed by a
 * {@link com.logopeda.reminder.service.ReminderDeliveryPort} (mock/logging by
 * default, disabled when configured). Reminders are purely internal: no external
 * clinical automation is performed.
 */
@Entity
@Table(name = "reminders", indexes = {
        @Index(name = "idx_reminder_clinic", columnList = "clinicId"),
        @Index(name = "idx_reminder_status_time", columnList = "status,remindAt"),
        @Index(name = "idx_reminder_target_user", columnList = "targetUserId")
})
public class Reminder extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(length = 36)
    private String patientId;

    @Column(length = 36)
    private String targetUserId;

    @Column(length = 36)
    private String createdByUserId;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String message;

    @Column(nullable = false)
    private Instant remindAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ReminderStatus status = ReminderStatus.SCHEDULED;

    @Column(length = 40)
    private String relatedEntityType;

    @Column(length = 36)
    private String relatedEntityId;

    private Instant sentAt;

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getRemindAt() {
        return remindAt;
    }

    public void setRemindAt(Instant remindAt) {
        this.remindAt = remindAt;
    }

    public ReminderStatus getStatus() {
        return status;
    }

    public void setStatus(ReminderStatus status) {
        this.status = status;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }

    public String getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(String relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }
}
