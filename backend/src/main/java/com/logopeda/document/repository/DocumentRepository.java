package com.logopeda.document.repository;

import com.logopeda.document.model.Document;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

    Optional<Document> findByIdAndClinicId(String id, String clinicId);

    List<Document> findByPatientIdAndClinicIdOrderByCreatedAtDesc(String patientId, String clinicId);

    List<Document> findByPatientIdAndClinicIdAndVisibleToFamilyTrueOrderByCreatedAtDesc(
            String patientId, String clinicId);
}
