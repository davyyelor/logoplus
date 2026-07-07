package com.logopeda.questionnaire.model;

import com.logopeda.questionnaire.enums.QuestionnaireTargetRole;
import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/** A parametrizable questionnaire template owned by a clinic. */
@Entity
@Table(name = "questionnaire_templates", indexes = {
        @Index(name = "idx_qtemplate_clinic", columnList = "clinicId")
})
public class QuestionnaireTemplate extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private QuestionnaireTargetRole targetRole = QuestionnaireTargetRole.BOTH;

    @Column(nullable = false)
    private boolean active = true;

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QuestionnaireTargetRole getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(QuestionnaireTargetRole targetRole) {
        this.targetRole = targetRole;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
