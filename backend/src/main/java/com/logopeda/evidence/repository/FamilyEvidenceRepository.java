package com.logopeda.evidence.repository;

import com.logopeda.evidence.enums.EvidenceReviewStatus;
import com.logopeda.evidence.model.FamilyEvidence;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyEvidenceRepository extends JpaRepository<FamilyEvidence, String> {

    Optional<FamilyEvidence> findByIdAndClinicId(String id, String clinicId);

    List<FamilyEvidence> findByPatientIdAndClinicIdOrderByCreatedAtDesc(String patientId, String clinicId);

    List<FamilyEvidence> findByClinicIdAndReviewStatusOrderByCreatedAtDesc(
            String clinicId, EvidenceReviewStatus reviewStatus);
}
