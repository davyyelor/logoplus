package com.logopeda.report.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

/**
 * Parameters for generating a report draft. The clinician chooses the template,
 * an optional session date range and which goals to include.
 */
public record GenerateReportRequest(
        @NotBlank(message = "templateId is required")
        String templateId,

        String title,

        boolean includeSessions,

        LocalDate sessionsFrom,

        LocalDate sessionsTo,

        boolean includeGoals,

        /** When provided, only these goal ids are summarized; otherwise all goals. */
        List<String> goalIds,

        String recommendations) {
}
