package com.logopeda.patient.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.logopeda.billing.BillingApplication;
import com.logopeda.patient.enums.PatientStatus;
import com.logopeda.patient.model.Patient;
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
 * Verifies the dynamic {@link PatientSpecifications} search, focusing on the
 * case-insensitive text filter. The {@code lower()} predicate is only added
 * when the search term is non-blank and is applied exclusively to the
 * {@code String} name columns, so PostgreSQL never sees {@code lower(bytea)}.
 */
@DataJpaTest
@ContextConfiguration(classes = BillingApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PatientSearchRepositoryTest {

    private static final String CLINIC_A = "clinic-a";
    private static final String CLINIC_B = "clinic-b";

    @Autowired
    private PatientRepository repository;

    @BeforeEach
    void seed() {
        repository.deleteAll();
        repository.save(patient(CLINIC_A, "Ana", "Garcia", PatientStatus.ACTIVE, "therapist-1"));
        repository.save(patient(CLINIC_A, "Bruno", "Martinez", PatientStatus.INACTIVE, "therapist-2"));
        repository.save(patient(CLINIC_A, "Carla", "GARCIA", PatientStatus.ACTIVE, "therapist-1"));
        repository.save(patient(CLINIC_B, "Ana", "Garcia", PatientStatus.ACTIVE, "therapist-1"));
    }

    private List<Patient> search(String clinicId, PatientStatus status, String therapistId, String term) {
        Specification<Patient> spec = Specification
                .where(PatientSpecifications.belongsToClinic(clinicId))
                .and(PatientSpecifications.hasStatus(status))
                .and(PatientSpecifications.hasMainTherapist(therapistId))
                .and(PatientSpecifications.nameContains(term));
        return repository.findAll(spec, Sort.by(Sort.Order.asc("lastName"), Sort.Order.asc("firstName")));
    }

    @Test
    void searchByClinicOnly() {
        List<Patient> result = search(CLINIC_A, null, null, null);
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Patient::getClinicId).containsOnly(CLINIC_A);
    }

    @Test
    void nullSearchTermDoesNotEmitLowerAndReturnsAll() {
        List<Patient> result = search(CLINIC_A, null, null, null);
        assertThat(result).hasSize(3);
    }

    @Test
    void blankSearchTermIsIgnored() {
        List<Patient> result = search(CLINIC_A, null, null, "   ");
        assertThat(result).hasSize(3);
    }

    @Test
    void mixedCaseSearchTermMatchesCaseInsensitively() {
        List<Patient> lower = search(CLINIC_A, null, null, "garcia");
        List<Patient> upper = search(CLINIC_A, null, null, "GARCIA");
        List<Patient> mixed = search(CLINIC_A, null, null, "GaRcIa");

        assertThat(lower).extracting(Patient::getFirstName).containsExactlyInAnyOrder("Ana", "Carla");
        assertThat(upper).extracting(Patient::getFirstName).containsExactlyInAnyOrder("Ana", "Carla");
        assertThat(mixed).extracting(Patient::getFirstName).containsExactlyInAnyOrder("Ana", "Carla");
    }

    @Test
    void searchByStatus() {
        List<Patient> result = search(CLINIC_A, PatientStatus.ACTIVE, null, null);
        assertThat(result).extracting(Patient::getStatus).containsOnly(PatientStatus.ACTIVE);
        assertThat(result).hasSize(2);
    }

    @Test
    void neverReturnsPatientsFromAnotherClinic() {
        List<Patient> result = search(CLINIC_A, null, null, "ana");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClinicId()).isEqualTo(CLINIC_A);
    }

    private Patient patient(String clinicId, String firstName, String lastName,
                            PatientStatus status, String therapistId) {
        Patient p = new Patient();
        p.setClinicId(clinicId);
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setStatus(status);
        p.setMainTherapistId(therapistId);
        return p;
    }
}
