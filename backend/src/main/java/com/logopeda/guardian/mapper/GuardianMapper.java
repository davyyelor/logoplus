package com.logopeda.guardian.mapper;

import com.logopeda.guardian.dto.GuardianResponse;
import com.logopeda.guardian.model.Guardian;
import org.springframework.stereotype.Component;

@Component
public class GuardianMapper {

    public GuardianResponse toResponse(Guardian guardian) {
        return new GuardianResponse(
                guardian.getId(),
                guardian.getClinicId(),
                guardian.getPatientId(),
                guardian.getUserId(),
                guardian.getFirstName(),
                guardian.getLastName(),
                guardian.getFullName(),
                guardian.getRelationship(),
                guardian.getEmail(),
                guardian.getPhone(),
                guardian.isCanAccessPortal(),
                guardian.isCanReceiveReports(),
                guardian.isCanReceiveReminders(),
                guardian.getUserId() != null,
                guardian.getCreatedAt(),
                guardian.getUpdatedAt());
    }
}
