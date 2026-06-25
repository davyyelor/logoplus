package com.logopeda.patient.repository;

import com.logopeda.patient.enums.PatientStatus;
import com.logopeda.patient.model.Patient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {

    Optional<Patient> findByIdAndClinicId(String id, String clinicId);

    @Query("""
            SELECT p FROM Patient p
            WHERE p.clinicId = :clinicId
              AND (:status IS NULL OR p.status = :status)
              AND (:therapistId IS NULL OR p.mainTherapistId = :therapistId)
              AND (:search IS NULL OR
                   LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY p.lastName ASC, p.firstName ASC
            """)
    List<Patient> search(@Param("clinicId") String clinicId,
                         @Param("status") PatientStatus status,
                         @Param("therapistId") String therapistId,
                         @Param("search") String search);

    long countByClinicIdAndStatus(String clinicId, PatientStatus status);
}
