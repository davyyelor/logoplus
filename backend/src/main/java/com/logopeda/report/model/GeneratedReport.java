package com.logopeda.report.model;

import com.logopeda.report.enums.ReportStatus;
import com.logopeda.report.enums.ReportType;
import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;

/** A report generated for a patient from a template plus recorded data. */
@Entity
@Table(name = "generated_reports", indexes = {
        @Index(name = "idx_reports_clinic", columnList = "clinicId"),
        @Index(name = "idx_reports_patient", columnList = "patientId"),
        @Index(name = "idx_reports_status", columnList = "status")
})
public class GeneratedReport extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String patientId;

    @Column(length = 36)
    private String templateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ReportType reportType;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "text")
    private String content;

    /** Set once a PDF has been generated and stored as a document. */
    @Column(length = 36)
    private String pdfDocumentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private ReportStatus status = ReportStatus.DRAFT;

    private Instant generatedAt;

    @Column(length = 36)
    private String createdByUserId;

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

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPdfDocumentId() {
        return pdfDocumentId;
    }

    public void setPdfDocumentId(String pdfDocumentId) {
        this.pdfDocumentId = pdfDocumentId;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
