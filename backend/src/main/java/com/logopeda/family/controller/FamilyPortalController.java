package com.logopeda.family.controller;

import com.logopeda.appointment.dto.AppointmentResponse;
import com.logopeda.appointment.mapper.AppointmentMapper;
import com.logopeda.consent.dto.PatientConsentResponse;
import com.logopeda.consent.dto.SignConsentRequest;
import com.logopeda.consent.mapper.ConsentMapper;
import com.logopeda.consent.service.PatientConsentService;
import com.logopeda.document.dto.DocumentResponse;
import com.logopeda.document.enums.DocumentType;
import com.logopeda.document.mapper.DocumentMapper;
import com.logopeda.document.service.DocumentService;
import com.logopeda.family.service.FamilyPortalService;
import com.logopeda.patient.dto.PatientResponse;
import com.logopeda.patient.mapper.PatientMapper;
import com.logopeda.report.dto.GeneratedReportResponse;
import com.logopeda.report.mapper.ReportMapper;
import com.logopeda.therapy.dto.TherapySessionResponse;
import com.logopeda.therapy.mapper.TherapySessionMapper;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Family portal API. Every endpoint is restricted to the FAMILY role and is
 * additionally scoped — through {@link FamilyPortalService} and the shared
 * patient access guard — to the patients the authenticated guardian is linked to.
 * Families never receive data for patients they are not linked to.
 */
@RestController
@RequestMapping("/api/family")
@PreAuthorize("hasRole('FAMILY')")
public class FamilyPortalController {

    private final FamilyPortalService portalService;
    private final DocumentService documentService;
    private final PatientConsentService consentService;
    private final PatientMapper patientMapper;
    private final AppointmentMapper appointmentMapper;
    private final TherapySessionMapper sessionMapper;
    private final DocumentMapper documentMapper;
    private final ReportMapper reportMapper;
    private final ConsentMapper consentMapper;

    public FamilyPortalController(FamilyPortalService portalService, DocumentService documentService,
                                  PatientConsentService consentService, PatientMapper patientMapper,
                                  AppointmentMapper appointmentMapper, TherapySessionMapper sessionMapper,
                                  DocumentMapper documentMapper, ReportMapper reportMapper,
                                  ConsentMapper consentMapper) {
        this.portalService = portalService;
        this.documentService = documentService;
        this.consentService = consentService;
        this.patientMapper = patientMapper;
        this.appointmentMapper = appointmentMapper;
        this.sessionMapper = sessionMapper;
        this.documentMapper = documentMapper;
        this.reportMapper = reportMapper;
        this.consentMapper = consentMapper;
    }

    @GetMapping("/me/patients")
    public List<PatientResponse> myPatients() {
        return portalService.myPatients().stream().map(patientMapper::toResponse).toList();
    }

    @GetMapping("/patients/{patientId}/appointments")
    public List<AppointmentResponse> appointments(@PathVariable String patientId) {
        return portalService.appointments(patientId).stream().map(appointmentMapper::toResponse).toList();
    }

    @GetMapping("/patients/{patientId}/sessions")
    public List<TherapySessionResponse> sessions(@PathVariable String patientId) {
        return portalService.sessions(patientId).stream().map(sessionMapper::toResponse).toList();
    }

    @GetMapping("/patients/{patientId}/documents")
    public List<DocumentResponse> documents(@PathVariable String patientId) {
        return portalService.documents(patientId).stream().map(documentMapper::toResponse).toList();
    }

    @GetMapping("/patients/{patientId}/reports")
    public List<GeneratedReportResponse> reports(@PathVariable String patientId) {
        return portalService.reports(patientId).stream().map(reportMapper::toResponse).toList();
    }

    @GetMapping("/patients/{patientId}/consents")
    public List<PatientConsentResponse> consents(@PathVariable String patientId) {
        return portalService.consents(patientId).stream().map(consentMapper::toResponse).toList();
    }

    @PostMapping("/patients/{patientId}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse upload(@PathVariable String patientId,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam(value = "documentType", required = false) DocumentType documentType) {
        return documentMapper.toResponse(documentService.uploadByFamily(patientId, file, documentType));
    }

    @PatchMapping("/consents/{id}/sign")
    public PatientConsentResponse signConsent(@PathVariable String id,
                                              @Valid @RequestBody SignConsentRequest request) {
        return consentMapper.toResponse(consentService.sign(id, request));
    }
}
