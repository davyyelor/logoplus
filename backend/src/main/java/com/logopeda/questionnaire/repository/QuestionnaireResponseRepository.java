package com.logopeda.questionnaire.repository;

import com.logopeda.questionnaire.model.QuestionnaireResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionnaireResponseRepository extends JpaRepository<QuestionnaireResponse, String> {

    List<QuestionnaireResponse> findByAssignmentIdAndClinicIdOrderByCreatedAtDesc(
            String assignmentId, String clinicId);
}
