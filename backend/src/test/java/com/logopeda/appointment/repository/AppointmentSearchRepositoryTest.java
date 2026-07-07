package com.logopeda.appointment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.logopeda.appointment.enums.AppointmentStatus;
import com.logopeda.appointment.enums.LocationType;
import com.logopeda.appointment.model.Appointment;
import com.logopeda.billing.BillingApplication;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * Verifies the dynamic {@link AppointmentSpecifications} search. Running against
 * the H2 (PostgreSQL compatibility mode) datasource exercises the exact code
 * path used by {@code GET /api/appointments} without emitting untyped null
 * bind parameters.
 */
@DataJpaTest
@ContextConfiguration(classes = BillingApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AppointmentSearchRepositoryTest {

    private static final String CLINIC_A = "clinic-a";
    private static final String CLINIC_B = "clinic-b";
    private static final String PATIENT_1 = "patient-1";
    private static final String PATIENT_2 = "patient-2";
    private static final String THERAPIST_1 = "therapist-1";
    private static final String THERAPIST_2 = "therapist-2";

    private static final Instant DAY1 = Instant.parse("2026-07-07T09:00:00Z");
    private static final Instant DAY2 = Instant.parse("2026-07-08T09:00:00Z");
    private static final Instant DAY3 = Instant.parse("2026-07-09T09:00:00Z");

    @Autowired
    private AppointmentRepository repository;

    @BeforeEach
    void seed() {
        repository.deleteAll();
        // clinic A
        repository.save(appointment(CLINIC_A, PATIENT_1, THERAPIST_1, DAY2, AppointmentStatus.SCHEDULED));
        repository.save(appointment(CLINIC_A, PATIENT_2, THERAPIST_2, DAY1, AppointmentStatus.COMPLETED));
        repository.save(appointment(CLINIC_A, PATIENT_1, THERAPIST_2, DAY3, AppointmentStatus.CANCELLED));
        // clinic B (must never leak)
        repository.save(appointment(CLINIC_B, PATIENT_1, THERAPIST_1, DAY1, AppointmentStatus.SCHEDULED));
    }

    private Specification<Appointment> spec(String clinicId, String patientId, String therapistId,
                                            Instant from, Instant to, AppointmentStatus status) {
        return Specification
                .where(AppointmentSpecifications.belongsToClinic(clinicId))
                .and(AppointmentSpecifications.hasPatient(patientId))
                .and(AppointmentSpecifications.hasTherapist(therapistId))
                .and(AppointmentSpecifications.startsAtOrAfter(from))
                .and(AppointmentSpecifications.startsBefore(to))
                .and(AppointmentSpecifications.hasStatus(status));
    }

    private List<Appointment> search(String clinicId, String patientId, String therapistId,
                                     Instant from, Instant to, AppointmentStatus status) {
        return repository.findAll(spec(clinicId, patientId, therapistId, from, to, status),
                Sort.by(Sort.Direction.ASC, "startDateTime"));
    }

    @Test
    void searchByClinicOnlyReturnsAllForClinicOrderedAscending() {
        List<Appointment> result = search(CLINIC_A, null, null, null, null, null);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Appointment::getStartDateTime)
                .containsExactly(DAY1, DAY2, DAY3);
        assertThat(result).extracting(Appointment::getClinicId)
                .containsOnly(CLINIC_A);
    }

    @Test
    void searchByDateRangeUsesInclusiveFromAndExclusiveTo() {
        Instant from = Instant.parse("2026-07-07T00:00:00Z");
        Instant toExclusive = Instant.parse("2026-07-09T00:00:00Z"); // excludes DAY3

        List<Appointment> result = search(CLINIC_A, null, null, from, toExclusive, null);

        assertThat(result).extracting(Appointment::getStartDateTime)
                .containsExactly(DAY1, DAY2);
    }

    @Test
    void searchByPatient() {
        List<Appointment> result = search(CLINIC_A, PATIENT_1, null, null, null, null);

        assertThat(result).extracting(Appointment::getStartDateTime)
                .containsExactly(DAY2, DAY3);
        assertThat(result).extracting(Appointment::getPatientId).containsOnly(PATIENT_1);
    }

    @Test
    void searchByTherapist() {
        List<Appointment> result = search(CLINIC_A, null, THERAPIST_2, null, null, null);

        assertThat(result).extracting(Appointment::getStartDateTime)
                .containsExactly(DAY1, DAY3);
        assertThat(result).extracting(Appointment::getTherapistId).containsOnly(THERAPIST_2);
    }

    @Test
    void searchByStatus() {
        List<Appointment> result = search(CLINIC_A, null, null, null, null, AppointmentStatus.SCHEDULED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(result.get(0).getClinicId()).isEqualTo(CLINIC_A);
    }

    @Test
    void searchWithAllFiltersCombined() {
        Instant from = Instant.parse("2026-07-08T00:00:00Z");
        Instant toExclusive = Instant.parse("2026-07-09T00:00:00Z");

        List<Appointment> result = search(CLINIC_A, PATIENT_1, THERAPIST_1, from, toExclusive,
                AppointmentStatus.SCHEDULED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStartDateTime()).isEqualTo(DAY2);
    }

    @Test
    void nullFiltersReturnEverythingForTheClinic() {
        List<Appointment> result = search(CLINIC_A, null, null, null, null, null);
        assertThat(result).hasSize(3);
    }

    @Test
    void neverReturnsAppointmentsFromAnotherClinic() {
        List<Appointment> resultA = search(CLINIC_A, PATIENT_1, THERAPIST_1, null, null, null);
        // clinic B has a PATIENT_1 + THERAPIST_1 appointment that must be excluded
        assertThat(resultA).extracting(Appointment::getClinicId).containsOnly(CLINIC_A);

        List<Appointment> resultB = search(CLINIC_B, null, null, null, null, null);
        assertThat(resultB).hasSize(1);
        assertThat(resultB).extracting(Appointment::getClinicId).containsOnly(CLINIC_B);
    }

    private Appointment appointment(String clinicId, String patientId, String therapistId,
                                    Instant start, AppointmentStatus status) {
        Appointment a = new Appointment();
        a.setClinicId(clinicId);
        a.setPatientId(patientId);
        a.setTherapistId(therapistId);
        a.setTitle("Session");
        a.setStartDateTime(start);
        a.setEndDateTime(start.plus(1, ChronoUnit.HOURS));
        a.setStatus(status);
        a.setLocationType(LocationType.IN_PERSON);
        return a;
    }
}
