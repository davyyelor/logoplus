package com.logopeda.consent.service;

import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.consent.dto.ConsentTemplateRequest;
import com.logopeda.consent.model.ConsentTemplate;
import com.logopeda.consent.repository.ConsentTemplateRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CRUD for consent templates, scoped to the caller's clinic. */
@Service
@Transactional
public class ConsentTemplateService {

    private final ConsentTemplateRepository templateRepository;

    public ConsentTemplateService(ConsentTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Transactional(readOnly = true)
    public List<ConsentTemplate> list(String clinicId) {
        return templateRepository.findByClinicIdOrderByNameAsc(clinicId);
    }

    @Transactional(readOnly = true)
    public ConsentTemplate getById(String clinicId, String id) {
        return findOwned(clinicId, id);
    }

    public ConsentTemplate create(String clinicId, ConsentTemplateRequest request) {
        ConsentTemplate template = new ConsentTemplate();
        template.setClinicId(clinicId);
        template.setActive(true);
        apply(template, request);
        return templateRepository.save(template);
    }

    public ConsentTemplate update(String clinicId, String id, ConsentTemplateRequest request) {
        ConsentTemplate template = findOwned(clinicId, id);
        apply(template, request);
        return templateRepository.save(template);
    }

    public ConsentTemplate setActive(String clinicId, String id, boolean active) {
        ConsentTemplate template = findOwned(clinicId, id);
        template.setActive(active);
        return templateRepository.save(template);
    }

    private void apply(ConsentTemplate template, ConsentTemplateRequest request) {
        template.setName(request.name().trim());
        template.setConsentType(request.consentType());
        template.setBody(request.body());
    }

    private ConsentTemplate findOwned(String clinicId, String id) {
        return templateRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("ConsentTemplate", id));
    }
}
