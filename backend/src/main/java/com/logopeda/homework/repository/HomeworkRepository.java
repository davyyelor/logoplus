package com.logopeda.homework.repository;

import com.logopeda.homework.model.Homework;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, String> {

    Optional<Homework> findByIdAndClinicId(String id, String clinicId);

    List<Homework> findByPatientIdAndClinicIdOrderByCreatedAtDesc(String patientId, String clinicId);

    List<Homework> findByPatientIdAndClinicIdAndVisibleToFamilyTrueOrderByCreatedAtDesc(
            String patientId, String clinicId);
}
