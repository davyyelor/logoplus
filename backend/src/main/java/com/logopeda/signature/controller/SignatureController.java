package com.logopeda.signature.controller;

import com.logopeda.signature.dto.SignatureRequest;
import com.logopeda.signature.dto.SignatureResponse;
import com.logopeda.signature.dto.SignatureStatusResponse;
import com.logopeda.signature.mapper.SignatureMapper;
import com.logopeda.signature.service.SignatureService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Staff-facing electronic signature endpoints. Reading and creating signatures
 * is patient-scoped; the signature provider status is informational.
 */
@RestController
public class SignatureController {

    private final SignatureService signatureService;
    private final SignatureMapper signatureMapper;

    public SignatureController(SignatureService signatureService, SignatureMapper signatureMapper) {
        this.signatureService = signatureService;
        this.signatureMapper = signatureMapper;
    }

    @GetMapping("/api/signatures/status")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public SignatureStatusResponse status() {
        return signatureService.status();
    }

    @GetMapping("/api/patients/{patientId}/signatures")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public List<SignatureResponse> listForPatient(@PathVariable String patientId) {
        return signatureService.listForPatient(patientId)
                .stream().map(signatureMapper::toResponse).toList();
    }

    @GetMapping("/api/signatures/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public SignatureResponse get(@PathVariable String id) {
        return signatureMapper.toResponse(signatureService.getById(id));
    }

    @PostMapping("/api/patients/{patientId}/signatures")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public SignatureResponse sign(@PathVariable String patientId,
                                  @Valid @RequestBody SignatureRequest request) {
        return signatureMapper.toResponse(signatureService.sign(patientId, request));
    }
}
