package com.logopeda.report.repository;

import com.logopeda.report.model.GeneratedReport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratedReportRepository extends JpaRepository<GeneratedReport, String> {

    Optional<GeneratedReport> findByIdAndClinicId(String id, String clinicId);

    List<GeneratedReport> findByPatientIdAndClinicIdOrderByCreatedAtDesc(String patientId, String clinicId);

    long countByClinicId(String clinicId);
}
