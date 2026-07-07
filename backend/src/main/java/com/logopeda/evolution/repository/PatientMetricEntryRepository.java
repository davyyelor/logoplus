package com.logopeda.evolution.repository;

import com.logopeda.evolution.model.PatientMetricEntry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientMetricEntryRepository extends JpaRepository<PatientMetricEntry, String> {

    List<PatientMetricEntry> findByMetricIdAndClinicIdOrderByEntryDateAsc(String metricId, String clinicId);

    List<PatientMetricEntry> findByPatientIdAndClinicIdOrderByEntryDateAsc(String patientId, String clinicId);
}
