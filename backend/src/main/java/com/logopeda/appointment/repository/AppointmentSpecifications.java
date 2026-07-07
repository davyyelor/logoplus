package com.logopeda.appointment.repository;

import com.logopeda.appointment.enums.AppointmentStatus;
import com.logopeda.appointment.model.Appointment;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

/**
 * Dynamic {@link Specification} builders for {@link Appointment} queries.
 *
 * <p>Each optional filter returns {@code null} when the argument is not
 * supplied so that no predicate (and therefore no bind parameter) is emitted.
 * This avoids the {@code (:param is null or field = :param)} anti-pattern that
 * forces PostgreSQL to bind untyped null parameters, which triggers
 * {@code ERROR: could not determine data type of parameter}.
 */
public final class AppointmentSpecifications {

    private AppointmentSpecifications() {
    }

    /** Mandatory tenant filter enforcing multi-clinic isolation. */
    public static Specification<Appointment> belongsToClinic(String clinicId) {
        return (root, query, cb) -> cb.equal(root.get("clinicId"), clinicId);
    }

    public static Specification<Appointment> hasPatient(String patientId) {
        if (patientId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("patientId"), patientId);
    }

    public static Specification<Appointment> hasTherapist(String therapistId) {
        if (therapistId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("therapistId"), therapistId);
    }

    public static Specification<Appointment> startsAtOrAfter(Instant fromDateTime) {
        if (fromDateTime == null) {
            return null;
        }
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("startDateTime"), fromDateTime);
    }

    public static Specification<Appointment> startsBefore(Instant toDateTimeExclusive) {
        if (toDateTimeExclusive == null) {
            return null;
        }
        return (root, query, cb) ->
                cb.lessThan(root.get("startDateTime"), toDateTimeExclusive);
    }

    public static Specification<Appointment> hasStatus(AppointmentStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}
