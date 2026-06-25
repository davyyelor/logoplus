package com.logopeda.billing.repository;

import com.logopeda.billing.enums.SessionBillingStatus;
import com.logopeda.billing.model.SessionBilling;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionBillingRepository extends JpaRepository<SessionBilling, String> {

    @Query("""
            SELECT s FROM SessionBilling s
            WHERE s.clinicId = :clinicId
              AND (:patientId IS NULL OR s.patientId = :patientId)
              AND (:fromDate IS NULL OR s.sessionDate >= :fromDate)
              AND (:toDate IS NULL OR s.sessionDate <= :toDate)
              AND (:status IS NULL OR s.status = :status)
            ORDER BY s.sessionDate DESC, s.createdAt DESC
            """)
    List<SessionBilling> search(@Param("clinicId") String clinicId,
                               @Param("patientId") String patientId,
                               @Param("fromDate") LocalDate fromDate,
                               @Param("toDate") LocalDate toDate,
                               @Param("status") SessionBillingStatus status);

    List<SessionBilling> findByClinicIdAndStatus(String clinicId, SessionBillingStatus status);
}
