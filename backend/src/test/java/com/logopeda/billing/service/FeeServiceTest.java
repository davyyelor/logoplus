package com.logopeda.billing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.logopeda.billing.dto.FeeRequest;
import com.logopeda.billing.enums.RecurrenceType;
import com.logopeda.billing.model.Fee;
import com.logopeda.billing.repository.FeeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {

    private static final String CLINIC = "clinic-test";

    @Mock
    private FeeRepository feeRepository;

    @Mock
    private ClinicContext clinicContext;

    @InjectMocks
    private FeeService feeService;

    @BeforeEach
    void setUp() {
        when(feeRepository.save(any(Fee.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void createsActiveFee() {
        when(clinicContext.resolveCurrency(any())).thenReturn("EUR");
        FeeRequest request = new FeeRequest("patient-1", "Monthly plan",
                new BigDecimal("100.00"), "EUR", RecurrenceType.MONTHLY,
                LocalDate.now(), null, null);

        Fee created = feeService.create(CLINIC, request);

        assertThat(created.isActive()).isTrue();
        assertThat(created.getName()).isEqualTo("Monthly plan");
    }

    @Test
    void deactivateSetsActiveFalse() {
        Fee existing = new Fee();
        existing.setClinicId(CLINIC);
        existing.setActive(true);
        when(feeRepository.findById(anyString())).thenReturn(Optional.of(existing));

        Fee updated = feeService.setActive(CLINIC, "fee-1", false);

        assertThat(updated.isActive()).isFalse();
    }
}
