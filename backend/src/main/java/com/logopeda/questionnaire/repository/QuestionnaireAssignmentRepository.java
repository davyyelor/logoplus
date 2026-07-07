package com.logopeda.questionnaire.repository;

import com.logopeda.questionnaire.model.QuestionnaireAssignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionnaireAssignmentRepository extends JpaRepository<QuestionnaireAssignment, String> {

    Optional<QuestionnaireAssignment> findByIdAndClinicId(String id, String clinicId);

    List<QuestionnaireAssignment> findByPatientIdAndClinicIdOrderByCreatedAtDesc(String patientId, String clinicId);
}
