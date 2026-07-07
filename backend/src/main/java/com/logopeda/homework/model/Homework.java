package com.logopeda.homework.model;

import com.logopeda.homework.enums.HomeworkStatus;
import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;

/** A home task assigned by clinical staff to a patient. */
@Entity
@Table(name = "homework", indexes = {
        @Index(name = "idx_homework_clinic", columnList = "clinicId"),
        @Index(name = "idx_homework_patient", columnList = "patientId"),
        @Index(name = "idx_homework_status", columnList = "status")
})
public class Homework extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String patientId;

    @Column(length = 36)
    private String sessionId;

    @Column(length = 36)
    private String createdByUserId;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(length = 4000)
    private String instructions;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private HomeworkStatus status = HomeworkStatus.ASSIGNED;

    @Column(nullable = false)
    private boolean visibleToFamily = true;

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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public HomeworkStatus getStatus() {
        return status;
    }

    public void setStatus(HomeworkStatus status) {
        this.status = status;
    }

    public boolean isVisibleToFamily() {
        return visibleToFamily;
    }

    public void setVisibleToFamily(boolean visibleToFamily) {
        this.visibleToFamily = visibleToFamily;
    }
}
