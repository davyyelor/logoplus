package com.logopeda.consent.controller;

import com.logopeda.consent.dto.IssueConsentRequest;
import com.logopeda.consent.dto.PatientConsentResponse;
import com.logopeda.consent.dto.SignConsentRequest;
import com.logopeda.consent.mapper.ConsentMapper;
import com.logopeda.consent.service.PatientConsentService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Patient consent lifecycle. Staff issue and revoke consents; staff and FAMILY
 * users may sign and download the PDF (always patient-scoped). Issuing is limited
 * to staff so families cannot create new consent records.
 */
@RestController
public class PatientConsentController {

    private final PatientConsentService consentService;
    private final ConsentMapper consentMapper;

    public PatientConsentController(PatientConsentService consentService, ConsentMapper consentMapper) {
        this.consentService = consentService;
        this.consentMapper = consentMapper;
    }

    @GetMapping("/api/patients/{patientId}/consents")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public List<PatientConsentResponse> listForPatient(@PathVariable String patientId) {
        return consentService.listForPatient(patientId)
                .stream().map(consentMapper::toResponse).toList();
    }

    @GetMapping("/api/consents/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public PatientConsentResponse get(@PathVariable String id) {
        return consentMapper.toResponse(consentService.getById(id));
    }

    @PostMapping("/api/patients/{patientId}/consents")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public PatientConsentResponse issue(@PathVariable String patientId,
                                        @Valid @RequestBody IssueConsentRequest request) {
        return consentMapper.toResponse(consentService.issue(patientId, request));
    }

    @PatchMapping("/api/consents/{id}/sign")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION','FAMILY')")
    public PatientConsentResponse sign(@PathVariable String id,
                                       @Valid @RequestBody SignConsentRequest request) {
        return consentMapper.toResponse(consentService.sign(id, request));
    }

    @PatchMapping("/api/consents/{id}/revoke")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public PatientConsentResponse revoke(@PathVariable String id) {
        return consentMapper.toResponse(consentService.revoke(id));
    }

    @GetMapping("/api/consents/{id}/pdf")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION','FAMILY')")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String id) {
        byte[] pdf = consentService.renderPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"consent-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
