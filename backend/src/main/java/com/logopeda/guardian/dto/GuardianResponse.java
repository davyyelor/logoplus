package com.logopeda.guardian.dto;

import com.logopeda.guardian.enums.GuardianRelationship;
import java.time.Instant;

public record GuardianResponse(
        String id,
        String clinicId,
        String patientId,
        String userId,
        String firstName,
        String lastName,
        String fullName,
        GuardianRelationship relationship,
        String email,
        String phone,
        boolean canAccessPortal,
        boolean canReceiveReports,
        boolean canReceiveReminders,
        boolean hasPortalAccount,
        Instant createdAt,
        Instant updatedAt) {
}
