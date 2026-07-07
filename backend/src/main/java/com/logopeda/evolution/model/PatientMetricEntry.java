package com.logopeda.evolution.model;

import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;

/** A single recorded value for a {@link PatientMetric}. */
@Entity
@Table(name = "patient_metric_entries", indexes = {
        @Index(name = "idx_metric_entry_clinic", columnList = "clinicId"),
        @Index(name = "idx_metric_entry_patient", columnList = "patientId"),
        @Index(name = "idx_metric_entry_metric", columnList = "metricId")
})
public class PatientMetricEntry extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String patientId;

    @Column(nullable = false, length = 36)
    private String metricId;

    @Column(length = 36)
    private String sessionId;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private LocalDate entryDate;

    @Column(length = 1000)
    private String notes;

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

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
