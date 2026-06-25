package com.logopeda.appointment.repository;

import com.logopeda.appointment.enums.AppointmentStatus;
import com.logopeda.appointment.model.Appointment;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    Optional<Appointment> findByIdAndClinicId(String id, String clinicId);

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.clinicId = :clinicId
              AND (:patientId IS NULL OR a.patientId = :patientId)
              AND (:therapistId IS NULL OR a.therapistId = :therapistId)
              AND (:from IS NULL OR a.startDateTime >= :from)
              AND (:to IS NULL OR a.startDateTime <= :to)
              AND (:status IS NULL OR a.status = :status)
            ORDER BY a.startDateTime ASC
            """)
    List<Appointment> search(@Param("clinicId") String clinicId,
                            @Param("patientId") String patientId,
                            @Param("therapistId") String therapistId,
                            @Param("from") Instant from,
                            @Param("to") Instant to,
                            @Param("status") AppointmentStatus status);

    List<Appointment> findByPatientIdAndClinicIdOrderByStartDateTimeDesc(String patientId, String clinicId);

    long countByClinicIdAndStartDateTimeBetween(String clinicId, Instant from, Instant to);
}
