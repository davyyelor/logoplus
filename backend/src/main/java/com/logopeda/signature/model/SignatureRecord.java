package com.logopeda.signature.model;

import com.logopeda.shared.model.BaseEntity;
import com.logopeda.signature.enums.SignatureProviderType;
import com.logopeda.signature.enums.SignatureStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * An electronic signature ledger entry. Each record captures who signed which
 * document, when, with which provider and the resulting verifiable hash.
 * {@code clinicId} is the tenant boundary and {@code patientId} scopes the
 * record so FAMILY users only ever see signatures for linked patients.
 */
@Entity
@Table(name = "signature_records", indexes = {
        @Index(name = "idx_signature_clinic", columnList = "clinicId"),
        @Index(name = "idx_signature_patient", columnList = "patientId")
})
public class SignatureRecord extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String patientId;

    @Column(nullable = false, length = 40)
    private String documentType;

    @Column(length = 36)
    private String documentId;

    @Column(length = 36)
    private String signerUserId;

    @Column(nullable = false)
    private String signerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SignatureProviderType provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SignatureStatus status;

    @Column(length = 128)
    private String signatureHash;

    @Column(length = 1000)
    private String note;

    private Instant signedAt;

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

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getSignerUserId() {
        return signerUserId;
    }

    public void setSignerUserId(String signerUserId) {
        this.signerUserId = signerUserId;
    }

    public String getSignerName() {
        return signerName;
    }

    public void setSignerName(String signerName) {
        this.signerName = signerName;
    }

    public SignatureProviderType getProvider() {
        return provider;
    }

    public void setProvider(SignatureProviderType provider) {
        this.provider = provider;
    }

    public SignatureStatus getStatus() {
        return status;
    }

    public void setStatus(SignatureStatus status) {
        this.status = status;
    }

    public String getSignatureHash() {
        return signatureHash;
    }

    public void setSignatureHash(String signatureHash) {
        this.signatureHash = signatureHash;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(Instant signedAt) {
        this.signedAt = signedAt;
    }
}
