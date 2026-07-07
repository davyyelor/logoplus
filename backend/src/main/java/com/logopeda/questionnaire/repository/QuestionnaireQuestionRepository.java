package com.logopeda.questionnaire.repository;

import com.logopeda.questionnaire.model.QuestionnaireQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionnaireQuestionRepository extends JpaRepository<QuestionnaireQuestion, String> {

    List<QuestionnaireQuestion> findByTemplateIdAndClinicIdOrderByPositionAsc(String templateId, String clinicId);

    void deleteByTemplateIdAndClinicId(String templateId, String clinicId);
}
