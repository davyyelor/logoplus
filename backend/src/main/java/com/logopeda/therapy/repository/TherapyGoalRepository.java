package com.logopeda.therapy.repository;

import com.logopeda.therapy.model.TherapyGoal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TherapyGoalRepository extends JpaRepository<TherapyGoal, String> {

    Optional<TherapyGoal> findByIdAndClinicId(String id, String clinicId);

    List<TherapyGoal> findByPatientIdAndClinicIdOrderByCreatedAtDesc(String patientId, String clinicId);
}
