package com.logopeda.questionnaire.model;

import com.logopeda.questionnaire.enums.AssignmentStatus;
import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;

/** Assignment of a questionnaire template to a patient (and optionally a user). */
@Entity
@Table(name = "questionnaire_assignments", indexes = {
        @Index(name = "idx_qassign_clinic", columnList = "clinicId"),
        @Index(name = "idx_qassign_patient", columnList = "patientId"),
        @Index(name = "idx_qassign_template", columnList = "templateId")
})
public class QuestionnaireAssignment extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String patientId;

    @Column(nullable = false, length = 36)
    private String templateId;

    @Column(length = 36)
    private String assignedByUserId;

    @Column(length = 36)
    private String assignedToUserId;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AssignmentStatus status = AssignmentStatus.PENDING;

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

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getAssignedByUserId() {
        return assignedByUserId;
    }

    public void setAssignedByUserId(String assignedByUserId) {
        this.assignedByUserId = assignedByUserId;
    }

    public String getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(String assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }
}
