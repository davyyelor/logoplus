package com.logopeda.appointment.model;

import com.logopeda.appointment.enums.AppointmentStatus;
import com.logopeda.appointment.enums.LocationType;
import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

/** A scheduled appointment for a patient with a therapist. */
@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointments_clinic", columnList = "clinicId"),
        @Index(name = "idx_appointments_patient", columnList = "patientId"),
        @Index(name = "idx_appointments_therapist", columnList = "therapistId"),
        @Index(name = "idx_appointments_start", columnList = "startDateTime"),
        @Index(name = "idx_appointments_status", columnList = "status")
})
public class Appointment extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String patientId;

    @Column(nullable = false, length = 36)
    private String therapistId;

    private String title;

    @Column(nullable = false)
    private Instant startDateTime;

    @Column(nullable = false)
    private Instant endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private LocationType locationType = LocationType.IN_PERSON;

    @Column(length = 2000)
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

    public String getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(String therapistId) {
        this.therapistId = therapistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Instant startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Instant getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Instant endDateTime) {
        this.endDateTime = endDateTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
