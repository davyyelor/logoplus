package com.logopeda.patient.repository;

import com.logopeda.patient.enums.PatientStatus;
import com.logopeda.patient.model.Patient;
import jakarta.persistence.criteria.Predicate;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

/**
 * Dynamic {@link Specification} builders for {@link Patient} queries.
 *
 * <p>Optional filters return {@code null} when the argument is absent so no
 * predicate is emitted. The free-text filter is only added when it actually
 * contains text and {@code lower()} is applied exclusively to the
 * {@code String} columns {@code firstName}/{@code lastName}. This prevents the
 * {@code ERROR: function lower(bytea) does not exist} that PostgreSQL raises
 * when {@code lower()} receives an untyped null parameter.
 */
public final class PatientSpecifications {

    private PatientSpecifications() {
    }

    /** Mandatory tenant filter enforcing multi-clinic isolation. */
    public static Specification<Patient> belongsToClinic(String clinicId) {
        return (root, query, cb) -> cb.equal(root.get("clinicId"), clinicId);
    }

    public static Specification<Patient> hasStatus(PatientStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Patient> hasMainTherapist(String therapistId) {
        if (therapistId == null || therapistId.isBlank()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("mainTherapistId"), therapistId);
    }

    /** Case-insensitive match on first/last name; only applied when non-blank. */
    public static Specification<Patient> nameContains(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        String normalized = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> {
            Predicate first = cb.like(cb.lower(root.get("firstName")), normalized);
            Predicate last = cb.like(cb.lower(root.get("lastName")), normalized);
            return cb.or(first, last);
        };
    }
}
