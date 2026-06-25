package com.logopeda.report.service;

import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.report.dto.ReportTemplateRequest;
import com.logopeda.report.model.ReportTemplate;
import com.logopeda.report.repository.ReportTemplateRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CRUD for report templates, scoped to the caller's clinic. */
@Service
@Transactional
public class ReportTemplateService {

    private final ReportTemplateRepository templateRepository;

    public ReportTemplateService(ReportTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Transactional(readOnly = true)
    public List<ReportTemplate> list(String clinicId) {
        return templateRepository.findByClinicIdOrderByNameAsc(clinicId);
    }

    @Transactional(readOnly = true)
    public ReportTemplate getById(String clinicId, String id) {
        return findOwned(clinicId, id);
    }

    public ReportTemplate create(String clinicId, ReportTemplateRequest request) {
        ReportTemplate template = new ReportTemplate();
        template.setClinicId(clinicId);
        template.setActive(true);
        apply(template, request);
        return templateRepository.save(template);
    }

    public ReportTemplate update(String clinicId, String id, ReportTemplateRequest request) {
        ReportTemplate template = findOwned(clinicId, id);
        apply(template, request);
        return templateRepository.save(template);
    }

    public ReportTemplate setActive(String clinicId, String id, boolean active) {
        ReportTemplate template = findOwned(clinicId, id);
        template.setActive(active);
        return templateRepository.save(template);
    }

    private void apply(ReportTemplate template, ReportTemplateRequest request) {
        template.setName(request.name().trim());
        template.setReportType(request.reportType());
        template.setContentTemplate(request.contentTemplate());
    }

    private ReportTemplate findOwned(String clinicId, String id) {
        return templateRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("ReportTemplate", id));
    }
}
