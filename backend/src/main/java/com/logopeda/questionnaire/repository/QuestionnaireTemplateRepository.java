package com.logopeda.questionnaire.repository;

import com.logopeda.questionnaire.model.QuestionnaireTemplate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionnaireTemplateRepository extends JpaRepository<QuestionnaireTemplate, String> {

    Optional<QuestionnaireTemplate> findByIdAndClinicId(String id, String clinicId);

    List<QuestionnaireTemplate> findByClinicIdOrderByCreatedAtDesc(String clinicId);
}
