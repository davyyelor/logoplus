package com.logopeda.report.dto;

import com.logopeda.report.enums.ReportStatus;
import com.logopeda.report.enums.ReportType;
import java.time.Instant;

public record GeneratedReportResponse(
        String id,
        String clinicId,
        String patientId,
        String templateId,
        ReportType reportType,
        String title,
        String content,
        String pdfDocumentId,
        ReportStatus status,
        Instant generatedAt,
        String createdByUserId,
        Instant createdAt,
        Instant updatedAt) {
}
