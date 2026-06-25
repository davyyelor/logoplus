package com.logopeda.billing.controller;

import com.logopeda.billing.service.ClinicContext;
import com.logopeda.billing.service.CsvExportService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/exports")
public class ExportController {

    private static final MediaType CSV = MediaType.valueOf("text/csv");
    private static final Logger log = LoggerFactory.getLogger(ExportController.class);

    private final CsvExportService csvExportService;
    private final ClinicContext clinicContext;

    public ExportController(CsvExportService csvExportService, ClinicContext clinicContext) {
        this.csvExportService = csvExportService;
        this.clinicContext = clinicContext;
    }

    @GetMapping("/payments.csv")
    public ResponseEntity<byte[]> payments(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        String clinicId = clinicContext.resolveClinicId(clinicHeader);
        log.info("locura");
        return csv("payments.csv", csvExportService.paymentsCsv(clinicId, patientId, fromDate, toDate));
    }

    @GetMapping("/pending-sessions.csv")
    public ResponseEntity<byte[]> pendingSessions(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        String clinicId = clinicContext.resolveClinicId(clinicHeader);
        return csv("pending-sessions.csv",
                csvExportService.pendingSessionsCsv(clinicId, patientId, fromDate, toDate));
    }

    @GetMapping("/fees.csv")
    public ResponseEntity<byte[]> fees(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) Boolean active) {
        String clinicId = clinicContext.resolveClinicId(clinicHeader);
        return csv("fees.csv", csvExportService.feesCsv(clinicId, patientId, active));
    }

    @GetMapping("/billing-summary.csv")
    public ResponseEntity<byte[]> billingSummary(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        String clinicId = clinicContext.resolveClinicId(clinicHeader);
        return csv("billing-summary.csv", csvExportService.billingSummaryCsv(clinicId, fromDate, toDate));
    }

    private ResponseEntity<byte[]> csv(String fileName, String content) {
        byte[] body = content.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(CSV)
                .body(body);
    }
}
