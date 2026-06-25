package com.logopeda.patient.service;

import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.guardian.service.FamilyAccessService;
import com.logopeda.patient.model.Patient;
import com.logopeda.patient.repository.PatientRepository;
import com.logopeda.shared.enums.Role;
import com.logopeda.shared.security.AuthenticatedUser;
import com.logopeda.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Central authorization point for patient-scoped access. Staff roles are limited
 * to their clinic; FAMILY users are additionally limited to patients they are
 * linked to. Sub-resource controllers (sessions, documents, reports, ...) call
 * {@link #requireAccessiblePatient(String)} before serving any data so the
 * isolation rules live in one place.
 */
@Service
@Transactional(readOnly = true)
public class PatientAccessGuard {

    private final PatientRepository patientRepository;
    private final TenantContext tenantContext;
    private final FamilyAccessService familyAccessService;

    public PatientAccessGuard(PatientRepository patientRepository, TenantContext tenantContext,
                              FamilyAccessService familyAccessService) {
        this.patientRepository = patientRepository;
        this.tenantContext = tenantContext;
        this.familyAccessService = familyAccessService;
    }

    /**
     * Loads a patient ensuring the current user may access it. Throws
     * {@link ResourceNotFoundException} (not Forbidden) on cross-tenant access to
     * avoid leaking the existence of other clinics' patients.
     */
    public Patient requireAccessiblePatient(String patientId) {
        AuthenticatedUser user = tenantContext.requireCurrentUser();
        Patient patient = patientRepository.findByIdAndClinicId(patientId, user.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));
        if (user.getRole() == Role.FAMILY) {
            familyAccessService.assertCanAccess(user.getUserId(), patientId);
        }
        return patient;
    }
}
