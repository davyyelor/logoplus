package com.logopeda.therapy.model;

import com.logopeda.shared.model.BaseEntity;
import com.logopeda.therapy.enums.GoalProgressStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/** Progress recorded for a goal during a specific session. */
@Entity
@Table(name = "session_goal_progress", indexes = {
        @Index(name = "idx_sgp_clinic", columnList = "clinicId"),
        @Index(name = "idx_sgp_session", columnList = "sessionId"),
        @Index(name = "idx_sgp_goal", columnList = "goalId")
})
public class SessionGoalProgress extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String sessionId;

    @Column(nullable = false, length = 36)
    private String goalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private GoalProgressStatus progressStatus = GoalProgressStatus.WORKED;

    @Column(length = 2000)
    private String notes;

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public GoalProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(GoalProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
