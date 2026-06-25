package com.logopeda.therapy.model;

import com.logopeda.shared.model.BaseEntity;
import com.logopeda.therapy.enums.GoalPriority;
import com.logopeda.therapy.enums.GoalStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;

/** A therapeutic goal for a patient. */
@Entity
@Table(name = "therapy_goals", indexes = {
        @Index(name = "idx_goals_clinic", columnList = "clinicId"),
        @Index(name = "idx_goals_patient", columnList = "patientId"),
        @Index(name = "idx_goals_status", columnList = "status")
})
public class TherapyGoal extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String patientId;

    private String area;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GoalStatus status = GoalStatus.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GoalPriority priority = GoalPriority.MEDIUM;

    private LocalDate startDate;

    private LocalDate targetDate;

    private LocalDate achievedDate;

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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public GoalPriority getPriority() {
        return priority;
    }

    public void setPriority(GoalPriority priority) {
        this.priority = priority;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public LocalDate getAchievedDate() {
        return achievedDate;
    }

    public void setAchievedDate(LocalDate achievedDate) {
        this.achievedDate = achievedDate;
    }
}
