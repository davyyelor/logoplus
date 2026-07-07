package com.logopeda.therapy.model;

import com.logopeda.shared.model.BaseEntity;
import com.logopeda.therapy.enums.SessionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;

/** A therapy session record. May be linked to an appointment. */
@Entity
@Table(name = "therapy_sessions", indexes = {
        @Index(name = "idx_sessions_clinic", columnList = "clinicId"),
        @Index(name = "idx_sessions_patient", columnList = "patientId"),
        @Index(name = "idx_sessions_therapist", columnList = "therapistId"),
        @Index(name = "idx_sessions_date", columnList = "sessionDate")
})
public class TherapySession extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String patientId;

    @Column(nullable = false, length = 36)
    private String therapistId;

    @Column(length = 36)
    private String appointmentId;

    @Column(nullable = false)
    private LocalDate sessionDate;

    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private SessionType sessionType = SessionType.THERAPY;

    @Column(length = 4000)
    private String summary;

    @Column(length = 4000)
    private String activitiesPerformed;

    @Column(length = 4000)
    private String patientResponse;

    @Column(length = 4000)
    private String observations;

    @Column(length = 4000)
    private String homework;

    @Column(length = 4000)
    private String nextSteps;

    /** Optional center within the clinic where this session took place. */
    @Column(length = 36)
    private String centerId;

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

    public String getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(String therapistId) {
        this.therapistId = therapistId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getActivitiesPerformed() {
        return activitiesPerformed;
    }

    public void setActivitiesPerformed(String activitiesPerformed) {
        this.activitiesPerformed = activitiesPerformed;
    }

    public String getPatientResponse() {
        return patientResponse;
    }

    public void setPatientResponse(String patientResponse) {
        this.patientResponse = patientResponse;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getHomework() {
        return homework;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

    public String getNextSteps() {
        return nextSteps;
    }

    public void setNextSteps(String nextSteps) {
        this.nextSteps = nextSteps;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }
}
