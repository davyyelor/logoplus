package com.logopeda.report.dto;

import jakarta.validation.constraints.NotBlank;

/** Payload to edit a report draft's title/content before it is shared. */
public record UpdateReportRequest(
        @NotBlank(message = "title is required")
        String title,

        @NotBlank(message = "content is required")
        String content) {
}
