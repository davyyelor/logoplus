package com.logopeda.consent.mapper;

import com.logopeda.consent.dto.ConsentTemplateResponse;
import com.logopeda.consent.dto.PatientConsentResponse;
import com.logopeda.consent.model.ConsentTemplate;
import com.logopeda.consent.model.PatientConsent;
import org.springframework.stereotype.Component;

@Component
public class ConsentMapper {

    public ConsentTemplateResponse toResponse(ConsentTemplate template) {
        return new ConsentTemplateResponse(
                template.getId(),
                template.getClinicId(),
                template.getName(),
                template.getConsentType(),
                template.getBody(),
                template.isActive(),
                template.getCreatedAt(),
                template.getUpdatedAt());
    }

    public PatientConsentResponse toResponse(PatientConsent consent) {
        return new PatientConsentResponse(
                consent.getId(),
                consent.getClinicId(),
                consent.getPatientId(),
                consent.getTemplateId(),
                consent.getGuardianId(),
                consent.getConsentType(),
                consent.getTitle(),
                consent.getBody(),
                consent.getStatus(),
                consent.getSignatureText(),
                consent.getSignedByUserId(),
                consent.getSignedAt(),
                consent.getRevokedAt(),
                consent.getExpiresOn(),
                consent.getCreatedAt(),
                consent.getUpdatedAt());
    }
}
