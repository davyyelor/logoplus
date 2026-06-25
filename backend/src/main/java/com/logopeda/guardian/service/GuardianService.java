package com.logopeda.guardian.service;

import com.logopeda.audit.service.AuditService;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.guardian.dto.FamilyAccessResponse;
import com.logopeda.guardian.dto.GuardianRequest;
import com.logopeda.guardian.model.Guardian;
import com.logopeda.guardian.repository.GuardianRepository;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.shared.security.TenantContext;
import com.logopeda.user.model.User;
import com.logopeda.user.service.UserService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** Guardian management and family-portal account provisioning. */
@Service
@Transactional
public class GuardianService {

    private final GuardianRepository guardianRepository;
    private final PatientAccessGuard patientAccessGuard;
    private final UserService userService;
    private final TenantContext tenantContext;
    private final AuditService auditService;

    public GuardianService(GuardianRepository guardianRepository,
                           PatientAccessGuard patientAccessGuard, UserService userService,
                           TenantContext tenantContext, AuditService auditService) {
        this.guardianRepository = guardianRepository;
        this.patientAccessGuard = patientAccessGuard;
        this.userService = userService;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<Guardian> listForPatient(String patientId) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        return guardianRepository.findByPatientIdAndClinicIdOrderByCreatedAtAsc(
                patientId, tenantContext.requireClinicId());
    }

    public Guardian create(String patientId, GuardianRequest request) {
        patientAccessGuard.requireAccessiblePatient(patientId);
        Guardian guardian = new Guardian();
        guardian.setClinicId(tenantContext.requireClinicId());
        guardian.setPatientId(patientId);
        apply(guardian, request);
        Guardian saved = guardianRepository.save(guardian);
        auditService.record("GUARDIAN_CREATED", "Guardian", saved.getId(), null);
        return saved;
    }

    public Guardian update(String id, GuardianRequest request) {
        Guardian guardian = findOwned(id);
        apply(guardian, request);
        return guardianRepository.save(guardian);
    }

    public void delete(String id) {
        Guardian guardian = findOwned(id);
        guardianRepository.delete(guardian);
        auditService.record("GUARDIAN_DELETED", "Guardian", id, null);
    }

    /**
     * Creates a FAMILY portal account for the guardian and links it. Requires a
     * guardian email. The temporary password must be communicated to the family
     * out of band.
     */
    public FamilyAccessResponse createFamilyAccess(String id, String temporaryPassword) {
        Guardian guardian = findOwned(id);
        if (guardian.getUserId() != null) {
            throw new BusinessValidationException("This guardian already has portal access");
        }
        if (!StringUtils.hasText(guardian.getEmail())) {
            throw new BusinessValidationException("Guardian email is required to create portal access");
        }
        User user = userService.createFamilyUser(
                guardian.getClinicId(), guardian.getEmail(), temporaryPassword,
                guardian.getFirstName(), guardian.getLastName());
        guardian.setUserId(user.getId());
        guardian.setCanAccessPortal(true);
        guardianRepository.save(guardian);
        auditService.record("FAMILY_ACCESS_CREATED", "Guardian", guardian.getId(),
                "{\"userId\":\"" + user.getId() + "\"}");
        return new FamilyAccessResponse(guardian.getId(), user.getId(), user.getEmail());
    }

    private void apply(Guardian guardian, GuardianRequest request) {
        guardian.setFirstName(request.firstName().trim());
        guardian.setLastName(request.lastName().trim());
        guardian.setRelationship(request.relationship());
        guardian.setEmail(StringUtils.hasText(request.email()) ? request.email().trim().toLowerCase() : null);
        guardian.setPhone(request.phone());
        guardian.setCanAccessPortal(request.canAccessPortal());
        guardian.setCanReceiveReports(request.canReceiveReports());
        guardian.setCanReceiveReminders(request.canReceiveReminders());
    }

    private Guardian findOwned(String id) {
        Guardian guardian = guardianRepository.findByIdAndClinicId(id, tenantContext.requireClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Guardian", id));
        // Ensure the caller can access the linked patient (covers FAMILY callers).
        patientAccessGuard.requireAccessiblePatient(guardian.getPatientId());
        return guardian;
    }
}
