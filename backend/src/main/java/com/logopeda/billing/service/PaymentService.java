package com.logopeda.billing.service;

import com.logopeda.billing.dto.PaymentRequest;
import com.logopeda.billing.enums.PaymentMethod;
import com.logopeda.billing.enums.PaymentStatus;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.billing.model.Payment;
import com.logopeda.billing.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ClinicContext clinicContext;

    public PaymentService(PaymentRepository paymentRepository, ClinicContext clinicContext) {
        this.paymentRepository = paymentRepository;
        this.clinicContext = clinicContext;
    }

    @Transactional(readOnly = true)
    public List<Payment> search(String clinicId, String patientId, LocalDate fromDate,
                                LocalDate toDate, PaymentMethod method, PaymentStatus status) {
        return paymentRepository.search(clinicId, patientId, fromDate, toDate, method, status);
    }

    @Transactional(readOnly = true)
    public Payment getById(String clinicId, String id) {
        return findOwned(clinicId, id);
    }

    public Payment create(String clinicId, PaymentRequest request) {
        validateAmount(request.amount());
        Payment payment = new Payment();
        payment.setClinicId(clinicId);
        payment.setStatus(PaymentStatus.REGISTERED);
        apply(payment, request);
        return paymentRepository.save(payment);
    }

    public Payment update(String clinicId, String id, PaymentRequest request) {
        Payment payment = findOwned(clinicId, id);
        if (payment.getStatus() != PaymentStatus.REGISTERED) {
            throw new BusinessValidationException("Only REGISTERED payments can be edited");
        }
        validateAmount(request.amount());
        apply(payment, request);
        return paymentRepository.save(payment);
    }

    /** Logical cancellation. The record is kept for audit purposes. */
    public Payment cancel(String clinicId, String id) {
        Payment payment = findOwned(clinicId, id);
        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            return payment;
        }
        payment.setStatus(PaymentStatus.CANCELLED);
        return paymentRepository.save(payment);
    }

    public Payment refund(String clinicId, String id) {
        Payment payment = findOwned(clinicId, id);
        if (payment.getStatus() != PaymentStatus.REGISTERED) {
            throw new BusinessValidationException("Only REGISTERED payments can be refunded");
        }
        payment.setStatus(PaymentStatus.REFUNDED);
        return paymentRepository.save(payment);
    }

    private void apply(Payment payment, PaymentRequest request) {
        payment.setPatientId(request.patientId());
        payment.setSessionId(emptyToNull(request.sessionId()));
        payment.setFeeId(emptyToNull(request.feeId()));
        payment.setAmount(request.amount());
        payment.setCurrency(clinicContext.resolveCurrency(request.currency()));
        payment.setPaymentDate(request.paymentDate());
        payment.setMethod(request.method());
        payment.setNotes(request.notes());
    }

    private Payment findOwned(String clinicId, String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        if (!payment.getClinicId().equals(clinicId)) {
            // Do not leak existence of other clinics' data.
            throw new ResourceNotFoundException("Payment", id);
        }
        return payment;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessValidationException("amount must be positive");
        }
    }

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
