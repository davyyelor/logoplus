package com.logopeda.guardian.controller;

import com.logopeda.guardian.dto.CreateFamilyAccessRequest;
import com.logopeda.guardian.dto.FamilyAccessResponse;
import com.logopeda.guardian.dto.GuardianRequest;
import com.logopeda.guardian.dto.GuardianResponse;
import com.logopeda.guardian.mapper.GuardianMapper;
import com.logopeda.guardian.service.GuardianService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Guardian management for clinic staff. */
@RestController
@PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
public class GuardianController {

    private final GuardianService guardianService;
    private final GuardianMapper guardianMapper;

    public GuardianController(GuardianService guardianService, GuardianMapper guardianMapper) {
        this.guardianService = guardianService;
        this.guardianMapper = guardianMapper;
    }

    @GetMapping("/api/patients/{patientId}/guardians")
    public List<GuardianResponse> listForPatient(@PathVariable String patientId) {
        return guardianService.listForPatient(patientId)
                .stream().map(guardianMapper::toResponse).toList();
    }

    @PostMapping("/api/patients/{patientId}/guardians")
    @ResponseStatus(HttpStatus.CREATED)
    public GuardianResponse create(@PathVariable String patientId,
                                   @Valid @RequestBody GuardianRequest request) {
        return guardianMapper.toResponse(guardianService.create(patientId, request));
    }

    @PutMapping("/api/guardians/{id}")
    public GuardianResponse update(@PathVariable String id, @Valid @RequestBody GuardianRequest request) {
        return guardianMapper.toResponse(guardianService.update(id, request));
    }

    @DeleteMapping("/api/guardians/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        guardianService.delete(id);
    }

    @PostMapping("/api/guardians/{id}/create-family-access")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public FamilyAccessResponse createFamilyAccess(@PathVariable String id,
                                                   @Valid @RequestBody CreateFamilyAccessRequest request) {
        return guardianService.createFamilyAccess(id, request.temporaryPassword());
    }
}
