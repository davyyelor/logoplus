package com.logopeda.consent.model;

import com.logopeda.consent.enums.ConsentStatus;
import com.logopeda.consent.enums.ConsentType;
import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

/** A consent instance bound to a patient, capturing its lifecycle. */
@Entity
@Table(name = "patient_consents", indexes = {
        @Index(name = "idx_patient_consents_clinic", columnList = "clinicId"),
        @Index(name = "idx_patient_consents_patient", columnList = "patientId"),
        @Index(name = "idx_patient_consents_status", columnList = "status")
})
public class PatientConsent extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String patientId;

    @Column(length = 36)
    private String templateId;

    /** Optional guardian who signs on behalf of the patient. */
    @Column(length = 36)
    private String guardianId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private ConsentType consentType;

    @Column(nullable = false)
    private String title;

    /** Snapshot of the template body at the time the consent was issued. */
    @Lob
    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ConsentStatus status = ConsentStatus.PENDING;

    /** Typed signer name acting as a simple electronic signature. */
    private String signatureText;

    private String signedByUserId;

    private Instant signedAt;

    private Instant revokedAt;

    private LocalDate expiresOn;

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

    public String getGuardianId() {
        return guardianId;
    }

    public void setGuardianId(String guardianId) {
        this.guardianId = guardianId;
    }

    public ConsentType getConsentType() {
        return consentType;
    }

    public void setConsentType(ConsentType consentType) {
        this.consentType = consentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ConsentStatus getStatus() {
        return status;
    }

    public void setStatus(ConsentStatus status) {
        this.status = status;
    }

    public String getSignatureText() {
        return signatureText;
    }

    public void setSignatureText(String signatureText) {
        this.signatureText = signatureText;
    }

    public String getSignedByUserId() {
        return signedByUserId;
    }

    public void setSignedByUserId(String signedByUserId) {
        this.signedByUserId = signedByUserId;
    }

    public Instant getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(Instant signedAt) {
        this.signedAt = signedAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public LocalDate getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(LocalDate expiresOn) {
        this.expiresOn = expiresOn;
    }
}
