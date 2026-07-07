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
 * Family-facing electronic signature endpoints. Restricted to FAMILY and scoped
 * to linked patients via the access guard, so families can sign and review
 * signatures only for their own patients.
 */
@RestController
@PreAuthorize("hasRole('FAMILY')")
public class FamilySignatureController {

    private final SignatureService signatureService;
    private final SignatureMapper signatureMapper;

    public FamilySignatureController(SignatureService signatureService, SignatureMapper signatureMapper) {
        this.signatureService = signatureService;
        this.signatureMapper = signatureMapper;
    }

    @GetMapping("/api/family/signatures/status")
    public SignatureStatusResponse status() {
        return signatureService.status();
    }

    @GetMapping("/api/family/patients/{patientId}/signatures")
    public List<SignatureResponse> listForPatient(@PathVariable String patientId) {
        return signatureService.listForPatient(patientId)
                .stream().map(signatureMapper::toResponse).toList();
    }

    @PostMapping("/api/family/patients/{patientId}/signatures")
    @ResponseStatus(HttpStatus.CREATED)
    public SignatureResponse sign(@PathVariable String patientId,
                                  @Valid @RequestBody SignatureRequest request) {
        return signatureMapper.toResponse(signatureService.sign(patientId, request));
    }
}
