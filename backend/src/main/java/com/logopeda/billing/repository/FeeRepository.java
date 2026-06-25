package com.logopeda.billing.repository;

import com.logopeda.billing.model.Fee;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeRepository extends JpaRepository<Fee, String> {

    @Query("""
            SELECT f FROM Fee f
            WHERE f.clinicId = :clinicId
              AND (:patientId IS NULL OR f.patientId = :patientId)
              AND (:active IS NULL OR f.active = :active)
            ORDER BY f.createdAt DESC
            """)
    List<Fee> search(@Param("clinicId") String clinicId,
                     @Param("patientId") String patientId,
                     @Param("active") Boolean active);

    long countByClinicIdAndActiveTrue(String clinicId);
}
