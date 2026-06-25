package com.logopeda.guardian.service;

import com.logopeda.guardian.model.Guardian;
import com.logopeda.guardian.repository.GuardianRepository;
import com.logopeda.shared.security.ForbiddenException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Answers family-access questions: which patients a FAMILY user may see, and
 * whether a given FAMILY user is allowed to access a specific patient. This is
 * the single enforcement point for the rule "a family user must never see
 * patients they are not linked to".
 */
@Service
@Transactional(readOnly = true)
public class FamilyAccessService {

    private final GuardianRepository guardianRepository;

    public FamilyAccessService(GuardianRepository guardianRepository) {
        this.guardianRepository = guardianRepository;
    }

    /** Patient ids the given FAMILY user is linked to (portal-enabled guardians). */
    public List<String> accessiblePatientIds(String userId) {
        return guardianRepository.findByUserId(userId).stream()
                .filter(Guardian::isCanAccessPortal)
                .map(Guardian::getPatientId)
                .distinct()
                .toList();
    }

    public boolean canAccess(String userId, String patientId) {
        return guardianRepository.findByUserId(userId).stream()
                .anyMatch(g -> g.isCanAccessPortal() && g.getPatientId().equals(patientId));
    }

    /** Throws {@link ForbiddenException} when the family user has no link to the patient. */
    public void assertCanAccess(String userId, String patientId) {
        if (!canAccess(userId, patientId)) {
            throw new ForbiddenException("You do not have access to this patient");
        }
    }
}
