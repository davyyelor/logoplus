package com.logopeda.evidence.controller;

import com.logopeda.evidence.dto.FamilyEvidenceRequest;
import com.logopeda.evidence.dto.FamilyEvidenceResponse;
import com.logopeda.evidence.enums.EvidenceType;
import com.logopeda.evidence.mapper.FamilyEvidenceMapper;
import com.logopeda.evidence.service.FamilyEvidenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Family-facing evidence endpoints. Restricted to FAMILY and scoped to linked
 * patients via the service/access guard. Families may add text notes or upload
 * files as evidence for their patients' tasks and evolution.
 */
@RestController
@PreAuthorize("hasRole('FAMILY')")
public class FamilyEvidenceFamilyController {

    private final FamilyEvidenceService evidenceService;
    private final FamilyEvidenceMapper evidenceMapper;

    public FamilyEvidenceFamilyController(FamilyEvidenceService evidenceService,
                                          FamilyEvidenceMapper evidenceMapper) {
        this.evidenceService = evidenceService;
        this.evidenceMapper = evidenceMapper;
    }

    @PostMapping("/api/family/patients/{patientId}/evidence")
    @ResponseStatus(HttpStatus.CREATED)
    public FamilyEvidenceResponse createTextNote(@PathVariable String patientId,
                                                 @Valid @RequestBody FamilyEvidenceRequest request) {
        return evidenceMapper.toResponse(evidenceService.createTextNote(patientId, request));
    }

    @PostMapping("/api/family/patients/{patientId}/evidence/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public FamilyEvidenceResponse upload(@PathVariable String patientId,
                                         @RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "type", required = false) EvidenceType type,
                                         @RequestParam(value = "title", required = false) String title,
                                         @RequestParam(value = "description", required = false) String description,
                                         @RequestParam(value = "homeworkId", required = false) String homeworkId) {
        FamilyEvidenceRequest request = new FamilyEvidenceRequest(homeworkId, type, title, description, null);
        return evidenceMapper.toResponse(evidenceService.createUpload(patientId, file, request));
    }
}
