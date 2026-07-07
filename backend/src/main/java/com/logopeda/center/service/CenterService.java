package com.logopeda.center.service;

import com.logopeda.audit.service.AuditService;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.center.dto.CenterRequest;
import com.logopeda.center.model.Center;
import com.logopeda.center.repository.CenterRepository;
import com.logopeda.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages centers within the current clinic. {@code clinicId} remains the tenant
 * boundary; centers are an optional organizational layer inside a clinic.
 */
@Service
@Transactional
public class CenterService {

    private static final String ENTITY = "Center";

    private final CenterRepository centerRepository;
    private final TenantContext tenantContext;
    private final AuditService auditService;

    public CenterService(CenterRepository centerRepository, TenantContext tenantContext,
                         AuditService auditService) {
        this.centerRepository = centerRepository;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<Center> list() {
        return centerRepository.findByClinicIdOrderByNameAsc(tenantContext.requireClinicId());
    }

    @Transactional(readOnly = true)
    public Center getById(String id) {
        return findOwned(id);
    }

    public Center create(CenterRequest request) {
        Center center = new Center();
        center.setClinicId(tenantContext.requireClinicId());
        apply(center, request);
        Center saved = centerRepository.save(center);
        auditService.record("CENTER_CREATED", ENTITY, saved.getId(), null);
        return saved;
    }

    public Center update(String id, CenterRequest request) {
        Center center = findOwned(id);
        apply(center, request);
        return centerRepository.save(center);
    }

    public Center setActive(String id, boolean active) {
        Center center = findOwned(id);
        center.setActive(active);
        return centerRepository.save(center);
    }

    /**
     * Validates that the given center id (if provided) belongs to the current
     * clinic. Used by other modules before storing a {@code centerId}.
     */
    @Transactional(readOnly = true)
    public void requireCenterInClinic(String centerId) {
        if (centerId == null || centerId.isBlank()) {
            return;
        }
        if (!centerRepository.existsByIdAndClinicId(centerId, tenantContext.requireClinicId())) {
            throw new BusinessValidationException("Center does not belong to this clinic");
        }
    }

    private void apply(Center center, CenterRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new BusinessValidationException("Name is required");
        }
        center.setName(request.name().trim());
        center.setAddress(request.address());
        center.setCity(request.city());
        center.setPhone(request.phone());
        center.setEmail(request.email());
        if (request.active() != null) {
            center.setActive(request.active());
        }
    }

    private Center findOwned(String id) {
        return centerRepository.findByIdAndClinicId(id, tenantContext.requireClinicId())
                .orElseThrow(() -> new ResourceNotFoundException(ENTITY, id));
    }
}
