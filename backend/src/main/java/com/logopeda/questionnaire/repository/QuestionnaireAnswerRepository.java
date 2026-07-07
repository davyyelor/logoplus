package com.logopeda.questionnaire.repository;

import com.logopeda.questionnaire.model.QuestionnaireAnswer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionnaireAnswerRepository extends JpaRepository<QuestionnaireAnswer, String> {

    List<QuestionnaireAnswer> findByResponseIdAndClinicId(String responseId, String clinicId);
}
