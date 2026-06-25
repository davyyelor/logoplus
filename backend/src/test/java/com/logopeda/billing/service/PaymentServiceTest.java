package com.logopeda.billing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.logopeda.billing.dto.PaymentRequest;
import com.logopeda.billing.enums.PaymentMethod;
import com.logopeda.billing.enums.PaymentStatus;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.model.Payment;
import com.logopeda.billing.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final String CLINIC = "clinic-test";

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ClinicContext clinicContext;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createsRegisteredPayment() {
        when(clinicContext.resolveCurrency(any())).thenReturn("EUR");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        PaymentRequest request = new PaymentRequest("patient-1", null, null,
                new BigDecimal("30.00"), "EUR", LocalDate.now(), PaymentMethod.CASH, "note");

        Payment created = paymentService.create(CLINIC, request);

        assertThat(created.getStatus()).isEqualTo(PaymentStatus.REGISTERED);
        assertThat(created.getClinicId()).isEqualTo(CLINIC);
        assertThat(created.getCurrency()).isEqualTo("EUR");
    }

    @Test
    void refundChangesStatusToRefunded() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        Payment existing = new Payment();
        existing.setClinicId(CLINIC);
        existing.setStatus(PaymentStatus.REGISTERED);
        when(paymentRepository.findById(anyString())).thenReturn(Optional.of(existing));

        Payment refunded = paymentService.refund(CLINIC, "payment-1");

        assertThat(refunded.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }

    @Test
    void refundRejectsNonRegisteredPayment() {
        Payment existing = new Payment();
        existing.setClinicId(CLINIC);
        existing.setStatus(PaymentStatus.CANCELLED);
        when(paymentRepository.findById(anyString())).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> paymentService.refund(CLINIC, "payment-1"))
                .isInstanceOf(BusinessValidationException.class);
    }
}
