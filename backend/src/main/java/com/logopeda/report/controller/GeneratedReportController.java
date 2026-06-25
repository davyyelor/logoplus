package com.logopeda.report.controller;

import com.logopeda.report.dto.GenerateReportRequest;
import com.logopeda.report.dto.GeneratedReportResponse;
import com.logopeda.report.dto.UpdateReportRequest;
import com.logopeda.report.mapper.ReportMapper;
import com.logopeda.report.service.GeneratedReportService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Patient report generation and management. Generating, editing and sharing are
 * limited to clinical staff. The PDF endpoint is additionally readable by FAMILY
 * users for reports that were shared with them (enforced via patient access).
 */
@RestController
public class GeneratedReportController {

    private final GeneratedReportService reportService;
    private final ReportMapper reportMapper;

    public GeneratedReportController(GeneratedReportService reportService, ReportMapper reportMapper) {
        this.reportService = reportService;
        this.reportMapper = reportMapper;
    }

    @GetMapping("/api/patients/{patientId}/reports")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public List<GeneratedReportResponse> listForPatient(@PathVariable String patientId) {
        return reportService.listForPatient(patientId)
                .stream().map(reportMapper::toResponse).toList();
    }

    @GetMapping("/api/reports/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public GeneratedReportResponse get(@PathVariable String id) {
        return reportMapper.toResponse(reportService.getById(id));
    }

    @PostMapping("/api/patients/{patientId}/reports/generate")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public GeneratedReportResponse generate(@PathVariable String patientId,
                                            @Valid @RequestBody GenerateReportRequest request) {
        return reportMapper.toResponse(reportService.generate(patientId, request));
    }

    @PutMapping("/api/reports/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public GeneratedReportResponse update(@PathVariable String id,
                                          @Valid @RequestBody UpdateReportRequest request) {
        return reportMapper.toResponse(reportService.update(id, request));
    }

    @PatchMapping("/api/reports/{id}/share-with-family")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public GeneratedReportResponse shareWithFamily(@PathVariable String id) {
        return reportMapper.toResponse(reportService.shareWithFamily(id));
    }

    @PatchMapping("/api/reports/{id}/archive")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public GeneratedReportResponse archive(@PathVariable String id) {
        return reportMapper.toResponse(reportService.archive(id));
    }

    @GetMapping("/api/reports/{id}/pdf")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','FAMILY')")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String id) {
        byte[] pdf = reportService.renderPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
