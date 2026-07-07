package com.logopeda.evolution.model;

import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/** A named, quantitative metric tracked for a patient (no clinical inference). */
@Entity
@Table(name = "patient_metrics", indexes = {
        @Index(name = "idx_metric_clinic", columnList = "clinicId"),
        @Index(name = "idx_metric_patient", columnList = "patientId")
})
public class PatientMetric extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String patientId;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private String unit;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean visibleToFamily = false;

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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isVisibleToFamily() {
        return visibleToFamily;
    }

    public void setVisibleToFamily(boolean visibleToFamily) {
        this.visibleToFamily = visibleToFamily;
    }
}
