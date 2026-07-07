package com.logopeda.questionnaire.model;

import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

/** A submitted set of answers for a {@link QuestionnaireAssignment}. */
@Entity
@Table(name = "questionnaire_responses", indexes = {
        @Index(name = "idx_qresponse_clinic", columnList = "clinicId"),
        @Index(name = "idx_qresponse_assignment", columnList = "assignmentId")
})
public class QuestionnaireResponse extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String assignmentId;

    @Column(length = 36)
    private String respondedByUserId;

    private Instant submittedAt;

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getRespondedByUserId() {
        return respondedByUserId;
    }

    public void setRespondedByUserId(String respondedByUserId) {
        this.respondedByUserId = respondedByUserId;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }
}
