package com.logopeda.center.mapper;

import com.logopeda.center.dto.CenterResponse;
import com.logopeda.center.model.Center;
import org.springframework.stereotype.Component;

@Component
public class CenterMapper {

    public CenterResponse toResponse(Center center) {
        return new CenterResponse(
                center.getId(),
                center.getClinicId(),
                center.getName(),
                center.getAddress(),
                center.getCity(),
                center.getPhone(),
                center.getEmail(),
                center.isActive(),
                center.getCreatedAt(),
                center.getUpdatedAt());
    }
}
