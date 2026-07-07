package com.logopeda.patient.model;

import com.logopeda.patient.enums.Gender;
import com.logopeda.patient.enums.PatientStatus;
import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;

/** A patient belonging to a clinic. */
@Entity
@Table(name = "patients", indexes = {
        @Index(name = "idx_patients_clinic", columnList = "clinicId"),
        @Index(name = "idx_patients_status", columnList = "status"),
        @Index(name = "idx_patients_therapist", columnList = "mainTherapistId")
})
public class Patient extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PatientStatus status = PatientStatus.ACTIVE;

    /** Optional reference to the main therapist (a user id). */
    @Column(length = 36)
    private String mainTherapistId;

    private String schoolName;

    private String referralSource;

    @Column(length = 2000)
    private String reasonForConsultation;

    @Column(length = 4000)
    private String relevantNotes;

    /** Optional center within the clinic this patient is primarily attended at. */
    @Column(length = 36)
    private String centerId;

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public PatientStatus getStatus() {
        return status;
    }

    public void setStatus(PatientStatus status) {
        this.status = status;
    }

    public String getMainTherapistId() {
        return mainTherapistId;
    }

    public void setMainTherapistId(String mainTherapistId) {
        this.mainTherapistId = mainTherapistId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public String getReasonForConsultation() {
        return reasonForConsultation;
    }

    public void setReasonForConsultation(String reasonForConsultation) {
        this.reasonForConsultation = reasonForConsultation;
    }

    public String getRelevantNotes() {
        return relevantNotes;
    }

    public void setRelevantNotes(String relevantNotes) {
        this.relevantNotes = relevantNotes;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
