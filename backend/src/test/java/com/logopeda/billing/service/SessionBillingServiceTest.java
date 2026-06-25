package com.logopeda.billing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.logopeda.billing.enums.SessionBillingStatus;
import com.logopeda.billing.model.SessionBilling;
import com.logopeda.billing.repository.SessionBillingRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionBillingServiceTest {

    private static final String CLINIC = "clinic-test";

    @Mock
    private SessionBillingRepository repository;

    @Mock
    private ClinicContext clinicContext;

    @InjectMocks
    private SessionBillingService service;

    @BeforeEach
    void setUp() {
        when(repository.save(any(SessionBilling.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void markPaidSetsPaidAmountToFullAmount() {
        SessionBilling session = new SessionBilling();
        session.setClinicId(CLINIC);
        session.setAmount(new BigDecimal("50.00"));
        session.setPaidAmount(BigDecimal.ZERO);
        session.setStatus(SessionBillingStatus.PENDING);
        when(repository.findById(anyString())).thenReturn(Optional.of(session));

        SessionBilling result = service.markPaid(CLINIC, "session-1");

        assertThat(result.getStatus()).isEqualTo(SessionBillingStatus.PAID);
        assertThat(result.getPaidAmount()).isEqualByComparingTo("50.00");
    }

    @Test
    void markPendingResetsPaidAmount() {
        SessionBilling session = new SessionBilling();
        session.setClinicId(CLINIC);
        session.setAmount(new BigDecimal("50.00"));
        session.setPaidAmount(new BigDecimal("50.00"));
        session.setStatus(SessionBillingStatus.PAID);
        when(repository.findById(anyString())).thenReturn(Optional.of(session));

        SessionBilling result = service.markPending(CLINIC, "session-1");

        assertThat(result.getStatus()).isEqualTo(SessionBillingStatus.PENDING);
        assertThat(result.getPaidAmount()).isEqualByComparingTo("0");
    }
}
