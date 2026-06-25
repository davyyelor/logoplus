package com.logopeda.billing.mapper;

import com.logopeda.billing.dto.FeeResponse;
import com.logopeda.billing.model.Fee;
import org.springframework.stereotype.Component;

@Component
public class FeeMapper {

    public FeeResponse toResponse(Fee f) {
        return new FeeResponse(
                f.getId(),
                f.getClinicId(),
                f.getPatientId(),
                f.getName(),
                f.getAmount(),
                f.getCurrency(),
                f.getRecurrenceType(),
                f.getStartDate(),
                f.getEndDate(),
                f.isActive(),
                f.getNotes(),
                f.getCreatedAt(),
                f.getUpdatedAt());
    }
}
