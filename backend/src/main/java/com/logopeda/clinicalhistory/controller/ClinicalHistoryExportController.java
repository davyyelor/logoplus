package com.logopeda.clinicalhistory.controller;

import com.logopeda.clinicalhistory.enums.ExportFormat;
import com.logopeda.clinicalhistory.service.ClinicalHistoryExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Clinical history export for staff. Family users are excluded: the full history
 * is a staff-only artifact. Patient access is enforced by the service.
 */
@RestController
public class ClinicalHistoryExportController {

    private final ClinicalHistoryExportService exportService;

    public ClinicalHistoryExportController(ClinicalHistoryExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/api/patients/{patientId}/clinical-history/export")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public ResponseEntity<byte[]> export(@PathVariable String patientId,
                                         @RequestParam(defaultValue = "ZIP") ExportFormat format) {
        return switch (format) {
            case PDF -> file(exportService.exportPdf(patientId),
                    "historia-clinica-" + patientId + ".pdf", MediaType.APPLICATION_PDF);
            case CSV -> file(exportService.exportCsv(patientId),
                    "historia-clinica-" + patientId + ".csv",
                    MediaType.parseMediaType("text/csv; charset=UTF-8"));
            case ZIP -> file(exportService.exportZip(patientId),
                    "historia-clinica-" + patientId + ".zip",
                    MediaType.parseMediaType("application/zip"));
        };
    }

    private ResponseEntity<byte[]> file(byte[] body, String fileName, MediaType contentType) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(contentType)
                .body(body);
    }
}
