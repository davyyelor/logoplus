package com.logopeda.billing.config;

import com.logopeda.billing.enums.PaymentMethod;
import com.logopeda.billing.enums.PaymentStatus;
import com.logopeda.billing.enums.RecurrenceType;
import com.logopeda.billing.enums.SessionBillingStatus;
import com.logopeda.billing.model.Fee;
import com.logopeda.billing.model.Payment;
import com.logopeda.billing.model.SessionBilling;
import com.logopeda.billing.repository.FeeRepository;
import com.logopeda.billing.repository.PaymentRepository;
import com.logopeda.billing.repository.SessionBillingRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Loads a small, clearly-separated sample dataset for the in-memory H2 database
 * so the UI has something to render on first run. Enabled only when
 * {@code billing.seed-sample-data=true}. This is demo data, not production data.
 */
@Component
@ConditionalOnProperty(prefix = "billing", name = "seed-sample-data", havingValue = "true")
public class SampleDataSeeder implements CommandLineRunner {

    private final PaymentRepository paymentRepository;
    private final FeeRepository feeRepository;
    private final SessionBillingRepository sessionBillingRepository;
    private final BillingProperties properties;

    public SampleDataSeeder(PaymentRepository paymentRepository,
                            FeeRepository feeRepository,
                            SessionBillingRepository sessionBillingRepository,
                            BillingProperties properties) {
        this.paymentRepository = paymentRepository;
        this.feeRepository = feeRepository;
        this.sessionBillingRepository = sessionBillingRepository;
        this.properties = properties;
    }

    @Override
    public void run(String... args) {
        if (paymentRepository.count() > 0) {
            return;
        }
        String clinicId = properties.getDefaultClinicId();
        String currency = properties.getDefaultCurrency();

        Payment payment = new Payment();
        payment.setClinicId(clinicId);
        payment.setPatientId("patient-001");
        payment.setAmount(new BigDecimal("45.00"));
        payment.setCurrency(currency);
        payment.setPaymentDate(LocalDate.now().minusDays(3));
        payment.setMethod(PaymentMethod.CARD);
        payment.setStatus(PaymentStatus.REGISTERED);
        payment.setNotes("Session payment");
        paymentRepository.save(payment);

        Fee fee = new Fee();
        fee.setClinicId(clinicId);
        fee.setPatientId("patient-001");
        fee.setName("Monthly speech therapy plan");
        fee.setAmount(new BigDecimal("120.00"));
        fee.setCurrency(currency);
        fee.setRecurrenceType(RecurrenceType.MONTHLY);
        fee.setStartDate(LocalDate.now().withDayOfMonth(1));
        fee.setActive(true);
        feeRepository.save(fee);

        SessionBilling paidSession = new SessionBilling();
        paidSession.setClinicId(clinicId);
        paidSession.setPatientId("patient-001");
        paidSession.setSessionId("session-001");
        paidSession.setSessionDate(LocalDate.now().minusDays(3));
        paidSession.setAmount(new BigDecimal("45.00"));
        paidSession.setPaidAmount(new BigDecimal("45.00"));
        paidSession.setCurrency(currency);
        paidSession.setStatus(SessionBillingStatus.PAID);
        paidSession.setPaymentId(payment.getId());
        sessionBillingRepository.save(paidSession);

        SessionBilling pendingSession = new SessionBilling();
        pendingSession.setClinicId(clinicId);
        pendingSession.setPatientId("patient-002");
        pendingSession.setSessionId("session-002");
        pendingSession.setSessionDate(LocalDate.now().minusDays(1));
        pendingSession.setAmount(new BigDecimal("50.00"));
        pendingSession.setPaidAmount(BigDecimal.ZERO);
        pendingSession.setCurrency(currency);
        pendingSession.setStatus(SessionBillingStatus.PENDING);
        sessionBillingRepository.save(pendingSession);
    }
}
