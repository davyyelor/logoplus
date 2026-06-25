package com.logopeda.consent.repository;

import com.logopeda.consent.model.ConsentTemplate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsentTemplateRepository extends JpaRepository<ConsentTemplate, String> {

    Optional<ConsentTemplate> findByIdAndClinicId(String id, String clinicId);

    List<ConsentTemplate> findByClinicIdOrderByNameAsc(String clinicId);
}
