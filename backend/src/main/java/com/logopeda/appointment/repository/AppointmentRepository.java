package com.logopeda.appointment.repository;

import com.logopeda.appointment.model.Appointment;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String>,
        JpaSpecificationExecutor<Appointment> {

    Optional<Appointment> findByIdAndClinicId(String id, String clinicId);

    List<Appointment> findByPatientIdAndClinicIdOrderByStartDateTimeDesc(String patientId, String clinicId);

    long countByClinicIdAndStartDateTimeBetween(String clinicId, Instant from, Instant to);
}
