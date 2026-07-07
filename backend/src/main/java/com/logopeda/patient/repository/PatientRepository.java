package com.logopeda.patient.repository;

import com.logopeda.patient.model.Patient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String>,
        JpaSpecificationExecutor<Patient> {

    Optional<Patient> findByIdAndClinicId(String id, String clinicId);

    long countByClinicIdAndStatus(String clinicId, com.logopeda.patient.enums.PatientStatus status);
}
