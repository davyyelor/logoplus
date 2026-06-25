package com.logopeda.report.dto;

import com.logopeda.report.enums.ReportType;
import java.time.Instant;

public record ReportTemplateResponse(
        String id,
        String clinicId,
        String name,
        ReportType reportType,
        String contentTemplate,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {
}
