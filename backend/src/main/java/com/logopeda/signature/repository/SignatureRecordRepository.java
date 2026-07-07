package com.logopeda.signature.repository;

import com.logopeda.signature.model.SignatureRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignatureRecordRepository extends JpaRepository<SignatureRecord, String> {

    Optional<SignatureRecord> findByIdAndClinicId(String id, String clinicId);

    List<SignatureRecord> findByPatientIdAndClinicIdOrderByCreatedAtDesc(String patientId, String clinicId);
}
