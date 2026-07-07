package com.logopeda.evidence.controller;

import com.logopeda.evidence.dto.FamilyEvidenceResponse;
import com.logopeda.evidence.dto.ReviewEvidenceRequest;
import com.logopeda.evidence.mapper.FamilyEvidenceMapper;
import com.logopeda.evidence.service.FamilyEvidenceService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Staff-facing family-evidence endpoints: list a patient's evidence, list the
 * clinic's pending-review queue, and review (accept/reject) an item. Reads are
 * open to RECEPTION; reviews are limited to CLINIC_ADMIN and THERAPIST.
 */
@RestController
public class FamilyEvidenceController {

    private final FamilyEvidenceService evidenceService;
    private final FamilyEvidenceMapper evidenceMapper;

    public FamilyEvidenceController(FamilyEvidenceService evidenceService, FamilyEvidenceMapper evidenceMapper) {
        this.evidenceService = evidenceService;
        this.evidenceMapper = evidenceMapper;
    }

    @GetMapping("/api/patients/{patientId}/family-evidence")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public List<FamilyEvidenceResponse> listForPatient(@PathVariable String patientId) {
        return evidenceService.listForPatient(patientId).stream().map(evidenceMapper::toResponse).toList();
    }

    @GetMapping("/api/family-evidence/pending")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public List<FamilyEvidenceResponse> listPending() {
        return evidenceService.listPendingReview().stream().map(evidenceMapper::toResponse).toList();
    }

    @PatchMapping("/api/family-evidence/{id}/review")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public FamilyEvidenceResponse review(@PathVariable String id,
                                         @Valid @RequestBody ReviewEvidenceRequest request) {
        return evidenceMapper.toResponse(
                evidenceService.review(id, request.reviewStatus(), request.reviewNote()));
    }
}
