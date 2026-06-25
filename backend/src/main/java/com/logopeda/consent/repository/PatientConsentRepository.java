package com.logopeda.consent.repository;

import com.logopeda.consent.enums.ConsentStatus;
import com.logopeda.consent.model.PatientConsent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientConsentRepository extends JpaRepository<PatientConsent, String> {

    Optional<PatientConsent> findByIdAndClinicId(String id, String clinicId);

    List<PatientConsent> findByPatientIdAndClinicIdOrderByCreatedAtDesc(String patientId, String clinicId);

    long countByClinicIdAndStatus(String clinicId, ConsentStatus status);
}
