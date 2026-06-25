package com.logopeda.billing.service;

import com.logopeda.billing.dto.FeeRequest;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.billing.model.Fee;
import com.logopeda.billing.repository.FeeRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FeeService {

    private final FeeRepository feeRepository;
    private final ClinicContext clinicContext;

    public FeeService(FeeRepository feeRepository, ClinicContext clinicContext) {
        this.feeRepository = feeRepository;
        this.clinicContext = clinicContext;
    }

    @Transactional(readOnly = true)
    public List<Fee> search(String clinicId, String patientId, Boolean active) {
        return feeRepository.search(clinicId, patientId, active);
    }

    @Transactional(readOnly = true)
    public Fee getById(String clinicId, String id) {
        return findOwned(clinicId, id);
    }

    public Fee create(String clinicId, FeeRequest request) {
        validate(request);
        Fee fee = new Fee();
        fee.setClinicId(clinicId);
        fee.setActive(true);
        apply(fee, request);
        return feeRepository.save(fee);
    }

    public Fee update(String clinicId, String id, FeeRequest request) {
        validate(request);
        Fee fee = findOwned(clinicId, id);
        apply(fee, request);
        return feeRepository.save(fee);
    }

    public Fee setActive(String clinicId, String id, boolean active) {
        Fee fee = findOwned(clinicId, id);
        fee.setActive(active);
        return feeRepository.save(fee);
    }

    private void apply(Fee fee, FeeRequest request) {
        fee.setPatientId(request.patientId());
        fee.setName(request.name());
        fee.setAmount(request.amount());
        fee.setCurrency(clinicContext.resolveCurrency(request.currency()));
        fee.setRecurrenceType(request.recurrenceType());
        fee.setStartDate(request.startDate());
        fee.setEndDate(request.endDate());
        fee.setNotes(request.notes());
    }

    private void validate(FeeRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("amount must be positive");
        }
        if (request.endDate() != null && request.endDate().isBefore(request.startDate())) {
            throw new BusinessValidationException("endDate cannot be before startDate");
        }
    }

    private Fee findOwned(String clinicId, String id) {
        Fee fee = feeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee", id));
        if (!fee.getClinicId().equals(clinicId)) {
            throw new ResourceNotFoundException("Fee", id);
        }
        return fee;
    }
}
