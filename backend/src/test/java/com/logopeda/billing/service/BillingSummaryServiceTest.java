package com.logopeda.billing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.logopeda.billing.enums.PaymentStatus;
import com.logopeda.billing.enums.SessionBillingStatus;
import com.logopeda.billing.model.Payment;
import com.logopeda.billing.model.SessionBilling;
import com.logopeda.billing.repository.FeeRepository;
import com.logopeda.billing.repository.PaymentRepository;
import com.logopeda.billing.repository.SessionBillingRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BillingSummaryServiceTest {

    private static final String CLINIC = "clinic-test";

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SessionBillingRepository sessionBillingRepository;

    @Mock
    private FeeRepository feeRepository;

    @Mock
    private ClinicContext clinicContext;

    @InjectMocks
    private BillingSummaryService service;

    @Test
    void aggregatesTotalsAndCounts() {
        Payment registered = payment(new BigDecimal("45.00"), PaymentStatus.REGISTERED);
        Payment refunded = payment(new BigDecimal("20.00"), PaymentStatus.REFUNDED);
        when(paymentRepository.search(CLINIC, null, null, null, null, null))
                .thenReturn(List.of(registered, refunded));

        SessionBilling paid = session(new BigDecimal("45.00"), new BigDecimal("45.00"),
                SessionBillingStatus.PAID);
        SessionBilling pending = session(new BigDecimal("50.00"), BigDecimal.ZERO,
                SessionBillingStatus.PENDING);
        when(sessionBillingRepository.search(CLINIC, null, null, null, null))
                .thenReturn(List.of(paid, pending));

        when(feeRepository.countByClinicIdAndActiveTrue(CLINIC)).thenReturn(2L);
        when(clinicContext.defaultCurrency()).thenReturn("EUR");

        var summary = service.buildSummary(CLINIC, null, null);

        assertThat(summary.totalPaid()).isEqualByComparingTo("45.00");
        assertThat(summary.totalRefunded()).isEqualByComparingTo("20.00");
        assertThat(summary.totalPending()).isEqualByComparingTo("50.00");
        assertThat(summary.numberOfPaidSessions()).isEqualTo(1);
        assertThat(summary.numberOfPendingSessions()).isEqualTo(1);
        assertThat(summary.activeFees()).isEqualTo(2);
        assertThat(summary.currency()).isEqualTo("EUR");
    }

    private Payment payment(BigDecimal amount, PaymentStatus status) {
        Payment p = new Payment();
        p.setClinicId(CLINIC);
        p.setAmount(amount);
        p.setStatus(status);
        return p;
    }

    private SessionBilling session(BigDecimal amount, BigDecimal paid, SessionBillingStatus status) {
        SessionBilling s = new SessionBilling();
        s.setClinicId(CLINIC);
        s.setAmount(amount);
        s.setPaidAmount(paid);
        s.setStatus(status);
        return s;
    }
}
