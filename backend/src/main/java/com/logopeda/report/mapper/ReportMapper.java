package com.logopeda.report.mapper;

import com.logopeda.report.dto.GeneratedReportResponse;
import com.logopeda.report.dto.ReportTemplateResponse;
import com.logopeda.report.model.GeneratedReport;
import com.logopeda.report.model.ReportTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportTemplateResponse toResponse(ReportTemplate template) {
        return new ReportTemplateResponse(
                template.getId(),
                template.getClinicId(),
                template.getName(),
                template.getReportType(),
                template.getContentTemplate(),
                template.isActive(),
                template.getCreatedAt(),
                template.getUpdatedAt());
    }

    public GeneratedReportResponse toResponse(GeneratedReport report) {
        return new GeneratedReportResponse(
                report.getId(),
                report.getClinicId(),
                report.getPatientId(),
                report.getTemplateId(),
                report.getReportType(),
                report.getTitle(),
                report.getContent(),
                report.getPdfDocumentId(),
                report.getStatus(),
                report.getGeneratedAt(),
                report.getCreatedByUserId(),
                report.getCreatedAt(),
                report.getUpdatedAt());
    }
}
