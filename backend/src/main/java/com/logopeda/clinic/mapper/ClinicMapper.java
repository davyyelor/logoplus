package com.logopeda.clinic.mapper;

import com.logopeda.clinic.dto.ClinicResponse;
import com.logopeda.clinic.model.Clinic;
import org.springframework.stereotype.Component;

@Component
public class ClinicMapper {

    public ClinicResponse toResponse(Clinic clinic) {
        return new ClinicResponse(
                clinic.getId(),
                clinic.getName(),
                clinic.getLegalName(),
                clinic.getTaxId(),
                clinic.getEmail(),
                clinic.getPhone(),
                clinic.getAddress(),
                clinic.getCity(),
                clinic.getProvince(),
                clinic.getPostalCode(),
                clinic.getCountry(),
                clinic.getCreatedAt(),
                clinic.getUpdatedAt());
    }
}
