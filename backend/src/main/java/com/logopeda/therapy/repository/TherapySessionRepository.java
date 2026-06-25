package com.logopeda.therapy.repository;

import com.logopeda.therapy.enums.SessionType;
import com.logopeda.therapy.model.TherapySession;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TherapySessionRepository extends JpaRepository<TherapySession, String> {

    Optional<TherapySession> findByIdAndClinicId(String id, String clinicId);

    @Query("""
            SELECT s FROM TherapySession s
            WHERE s.clinicId = :clinicId
              AND (:patientId IS NULL OR s.patientId = :patientId)
              AND (:therapistId IS NULL OR s.therapistId = :therapistId)
              AND (:fromDate IS NULL OR s.sessionDate >= :fromDate)
              AND (:toDate IS NULL OR s.sessionDate <= :toDate)
              AND (:sessionType IS NULL OR s.sessionType = :sessionType)
            ORDER BY s.sessionDate DESC, s.createdAt DESC
            """)
    List<TherapySession> search(@Param("clinicId") String clinicId,
                               @Param("patientId") String patientId,
                               @Param("therapistId") String therapistId,
                               @Param("fromDate") LocalDate fromDate,
                               @Param("toDate") LocalDate toDate,
                               @Param("sessionType") SessionType sessionType);

    List<TherapySession> findByPatientIdAndClinicIdAndSessionDateBetweenOrderBySessionDateAsc(
            String patientId, String clinicId, LocalDate from, LocalDate to);

    List<TherapySession> findByPatientIdAndClinicIdOrderBySessionDateDesc(String patientId, String clinicId);
}
