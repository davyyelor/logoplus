package com.logopeda.clinic.service;

import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.clinic.dto.ClinicRequest;
import com.logopeda.clinic.model.Clinic;
import com.logopeda.clinic.repository.ClinicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class ClinicService {

    private final ClinicRepository clinicRepository;

    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    @Transactional(readOnly = true)
    public Clinic getById(String id) {
        return clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", id));
    }

    public Clinic update(String id, ClinicRequest request) {
        Clinic clinic = getById(id);
        apply(clinic, request);
        return clinicRepository.save(clinic);
    }

    /** Creates a clinic. Used during clinic-admin registration. */
    public Clinic create(ClinicRequest request) {
        Clinic clinic = new Clinic();
        apply(clinic, request);
        return clinicRepository.save(clinic);
    }

    private void apply(Clinic clinic, ClinicRequest request) {
        clinic.setName(request.name().trim());
        clinic.setLegalName(request.legalName());
        clinic.setTaxId(request.taxId());
        clinic.setEmail(request.email());
        clinic.setPhone(request.phone());
        clinic.setAddress(request.address());
        clinic.setCity(request.city());
        clinic.setProvince(request.province());
        clinic.setPostalCode(request.postalCode());
        clinic.setCountry(StringUtils.hasText(request.country()) ? request.country().toUpperCase() : "ES");
    }
}
