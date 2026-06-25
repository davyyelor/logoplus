package com.logopeda.consent.dto;

import com.logopeda.consent.enums.ConsentType;
import java.time.Instant;

public record ConsentTemplateResponse(
        String id,
        String clinicId,
        String name,
        ConsentType consentType,
        String body,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {
}
