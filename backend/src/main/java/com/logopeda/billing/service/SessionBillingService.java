package com.logopeda.billing.service;

import com.logopeda.billing.dto.SessionBillingRequest;
import com.logopeda.billing.enums.SessionBillingStatus;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.billing.model.SessionBilling;
import com.logopeda.billing.repository.SessionBillingRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessionBillingService {

    private final SessionBillingRepository repository;
    private final ClinicContext clinicContext;

    public SessionBillingService(SessionBillingRepository repository, ClinicContext clinicContext) {
        this.repository = repository;
        this.clinicContext = clinicContext;
    }

    @Transactional(readOnly = true)
    public List<SessionBilling> search(String clinicId, String patientId, LocalDate fromDate,
                                       LocalDate toDate, SessionBillingStatus status) {
        return repository.search(clinicId, patientId, fromDate, toDate, status);
    }

    @Transactional(readOnly = true)
    public SessionBilling getById(String clinicId, String id) {
        return findOwned(clinicId, id);
    }

    public SessionBilling create(String clinicId, SessionBillingRequest request) {
        SessionBilling session = new SessionBilling();
        session.setClinicId(clinicId);
        apply(session, request);
        session.setStatus(deriveStatus(session.getAmount(), session.getPaidAmount()));
        return repository.save(session);
    }

    public SessionBilling update(String clinicId, String id, SessionBillingRequest request) {
        SessionBilling session = findOwned(clinicId, id);
        apply(session, request);
        session.setStatus(deriveStatus(session.getAmount(), session.getPaidAmount()));
        return repository.save(session);
    }

    public SessionBilling markPaid(String clinicId, String id) {
        SessionBilling session = findOwned(clinicId, id);
        session.setPaidAmount(session.getAmount());
        session.setStatus(SessionBillingStatus.PAID);
        return repository.save(session);
    }

    public SessionBilling markPending(String clinicId, String id) {
        SessionBilling session = findOwned(clinicId, id);
        session.setPaidAmount(BigDecimal.ZERO);
        session.setPaymentId(null);
        session.setStatus(SessionBillingStatus.PENDING);
        return repository.save(session);
    }

    public SessionBilling markNoCharge(String clinicId, String id) {
        SessionBilling session = findOwned(clinicId, id);
        session.setPaidAmount(BigDecimal.ZERO);
        session.setStatus(SessionBillingStatus.NO_CHARGE);
        return repository.save(session);
    }

    private void apply(SessionBilling session, SessionBillingRequest request) {
        BigDecimal amount = request.amount() != null ? request.amount() : BigDecimal.ZERO;
        BigDecimal paid = request.paidAmount() != null ? request.paidAmount() : BigDecimal.ZERO;
        if (paid.compareTo(amount) > 0) {
            throw new BusinessValidationException("paidAmount cannot be greater than amount");
        }
        session.setPatientId(request.patientId());
        session.setSessionId(request.sessionId());
        session.setSessionDate(request.sessionDate());
        session.setAmount(amount);
        session.setPaidAmount(paid);
        session.setCurrency(clinicContext.resolveCurrency(request.currency()));
        session.setNotes(request.notes());
    }

    private SessionBillingStatus deriveStatus(BigDecimal amount, BigDecimal paid) {
        if (paid.signum() == 0) {
            return SessionBillingStatus.PENDING;
        }
        if (paid.compareTo(amount) >= 0) {
            return SessionBillingStatus.PAID;
        }
        return SessionBillingStatus.PARTIALLY_PAID;
    }

    private SessionBilling findOwned(String clinicId, String id) {
        SessionBilling session = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SessionBilling", id));
        if (!session.getClinicId().equals(clinicId)) {
            throw new ResourceNotFoundException("SessionBilling", id);
        }
        return session;
    }
}
