package com.logopeda.patient.service;

import com.logopeda.audit.service.AuditService;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.center.service.CenterService;
import com.logopeda.patient.dto.PatientRequest;
import com.logopeda.patient.enums.PatientStatus;
import com.logopeda.patient.model.Patient;
import com.logopeda.patient.repository.PatientRepository;
import com.logopeda.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** Patient CRUD, always scoped to the caller's clinic. */
@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final TenantContext tenantContext;
    private final AuditService auditService;
    private final CenterService centerService;

    public PatientService(PatientRepository patientRepository, TenantContext tenantContext,
                          AuditService auditService, CenterService centerService) {
        this.patientRepository = patientRepository;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
        this.centerService = centerService;
    }

    @Transactional(readOnly = true)
    public List<Patient> search(String clinicId, PatientStatus status, String therapistId, String search) {
        return patientRepository.search(clinicId, status, emptyToNull(therapistId), emptyToNull(search));
    }

    @Transactional(readOnly = true)
    public Patient getById(String clinicId, String id) {
        return findOwned(clinicId, id);
    }

    public Patient create(String clinicId, PatientRequest request) {
        Patient patient = new Patient();
        patient.setClinicId(clinicId);
        patient.setStatus(PatientStatus.ACTIVE);
        apply(patient, request);
        Patient saved = patientRepository.save(patient);
        auditService.record("PATIENT_CREATED", "Patient", saved.getId(), null);
        return saved;
    }

    public Patient update(String clinicId, String id, PatientRequest request) {
        Patient patient = findOwned(clinicId, id);
        apply(patient, request);
        Patient saved = patientRepository.save(patient);
        auditService.record("PATIENT_UPDATED", "Patient", saved.getId(), null);
        return saved;
    }

    public Patient changeStatus(String clinicId, String id, PatientStatus status) {
        Patient patient = findOwned(clinicId, id);
        patient.setStatus(status);
        Patient saved = patientRepository.save(patient);
        auditService.record("PATIENT_STATUS_CHANGED", "Patient", saved.getId(),
                "{\"status\":\"" + status + "\"}");
        return saved;
    }

    /** Logical removal: marks the patient INACTIVE, preserving history. */
    public Patient softDelete(String clinicId, String id) {
        return changeStatus(clinicId, id, PatientStatus.INACTIVE);
    }

    @Transactional(readOnly = true)
    public long countActive(String clinicId) {
        return patientRepository.countByClinicIdAndStatus(clinicId, PatientStatus.ACTIVE);
    }

    private void apply(Patient patient, PatientRequest request) {
        patient.setFirstName(request.firstName().trim());
        patient.setLastName(request.lastName().trim());
        patient.setBirthDate(request.birthDate());
        patient.setGender(request.gender());
        patient.setMainTherapistId(emptyToNull(request.mainTherapistId()));
        patient.setSchoolName(request.schoolName());
        patient.setReferralSource(request.referralSource());
        patient.setReasonForConsultation(request.reasonForConsultation());
        patient.setRelevantNotes(request.relevantNotes());
        String centerId = emptyToNull(request.centerId());
        centerService.requireCenterInClinic(centerId);
        patient.setCenterId(centerId);
    }

    private Patient findOwned(String clinicId, String id) {
        return patientRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
    }

    private String emptyToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
