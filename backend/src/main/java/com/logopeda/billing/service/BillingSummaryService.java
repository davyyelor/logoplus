package com.logopeda.billing.service;

import com.logopeda.billing.dto.BillingSummaryResponse;
import com.logopeda.billing.enums.PaymentStatus;
import com.logopeda.billing.enums.SessionBillingStatus;
import com.logopeda.billing.model.Payment;
import com.logopeda.billing.model.SessionBilling;
import com.logopeda.billing.repository.FeeRepository;
import com.logopeda.billing.repository.PaymentRepository;
import com.logopeda.billing.repository.SessionBillingRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BillingSummaryService {

    private final PaymentRepository paymentRepository;
    private final SessionBillingRepository sessionBillingRepository;
    private final FeeRepository feeRepository;
    private final ClinicContext clinicContext;

    public BillingSummaryService(PaymentRepository paymentRepository,
                                 SessionBillingRepository sessionBillingRepository,
                                 FeeRepository feeRepository,
                                 ClinicContext clinicContext) {
        this.paymentRepository = paymentRepository;
        this.sessionBillingRepository = sessionBillingRepository;
        this.feeRepository = feeRepository;
        this.clinicContext = clinicContext;
    }

    public BillingSummaryResponse buildSummary(String clinicId, LocalDate fromDate, LocalDate toDate) {
        List<Payment> payments = paymentRepository.search(clinicId, null, fromDate, toDate, null, null);

        BigDecimal totalPaid = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.REGISTERED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRefunded = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.REFUNDED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<SessionBilling> sessions =
                sessionBillingRepository.search(clinicId, null, fromDate, toDate, null);

        BigDecimal totalPending = sessions.stream()
                .filter(s -> s.getStatus() == SessionBillingStatus.PENDING
                        || s.getStatus() == SessionBillingStatus.PARTIALLY_PAID)
                .map(s -> s.getAmount().subtract(
                        s.getPaidAmount() != null ? s.getPaidAmount() : BigDecimal.ZERO))
                .map(value -> value.max(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long paidSessions = sessions.stream()
                .filter(s -> s.getStatus() == SessionBillingStatus.PAID)
                .count();

        long pendingSessions = sessions.stream()
                .filter(s -> s.getStatus() == SessionBillingStatus.PENDING
                        || s.getStatus() == SessionBillingStatus.PARTIALLY_PAID)
                .count();

        long activeFees = feeRepository.countByClinicIdAndActiveTrue(clinicId);

        return new BillingSummaryResponse(
                totalPaid,
                totalPending,
                totalRefunded,
                paidSessions,
                pendingSessions,
                activeFees,
                clinicContext.defaultCurrency());
    }
}
