package com.logopeda.report.repository;

import com.logopeda.report.model.ReportTemplate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, String> {

    Optional<ReportTemplate> findByIdAndClinicId(String id, String clinicId);

    List<ReportTemplate> findByClinicIdOrderByNameAsc(String clinicId);
}
