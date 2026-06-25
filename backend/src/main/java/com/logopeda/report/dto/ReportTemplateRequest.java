package com.logopeda.report.dto;

import com.logopeda.report.enums.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Payload to create or update a report template. */
public record ReportTemplateRequest(
        @NotBlank(message = "name is required")
        String name,

        @NotNull(message = "reportType is required")
        ReportType reportType,

        @NotBlank(message = "contentTemplate is required")
        String contentTemplate) {
}
