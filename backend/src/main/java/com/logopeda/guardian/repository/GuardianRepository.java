package com.logopeda.guardian.repository;

import com.logopeda.guardian.model.Guardian;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, String> {

    List<Guardian> findByPatientIdAndClinicIdOrderByCreatedAtAsc(String patientId, String clinicId);

    Optional<Guardian> findByIdAndClinicId(String id, String clinicId);

    /** Guardians linked to a portal (FAMILY) user account. */
    List<Guardian> findByUserId(String userId);

    boolean existsByUserIdAndPatientId(String userId, String patientId);
}
