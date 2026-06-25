package com.logopeda.notification.model;

import com.logopeda.notification.enums.NotificationType;
import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/** An in-app notification addressed to a single user. */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notifications_clinic", columnList = "clinicId"),
        @Index(name = "idx_notifications_user", columnList = "userId"),
        @Index(name = "idx_notifications_read", columnList = "readFlag")
})
public class Notification extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private NotificationType type = NotificationType.GENERAL;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String message;

    /** Optional deep-link target (e.g. "appointment:{id}") for the client. */
    @Column(length = 128)
    private String reference;

    @Column(nullable = false)
    private boolean readFlag = false;

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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public boolean isReadFlag() {
        return readFlag;
    }

    public void setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
    }
}
