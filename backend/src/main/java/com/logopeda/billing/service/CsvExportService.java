package com.logopeda.billing.service;

import com.logopeda.billing.dto.BillingSummaryResponse;
import com.logopeda.billing.enums.ExportType;
import com.logopeda.billing.enums.SessionBillingStatus;
import com.logopeda.billing.model.ExportLog;
import com.logopeda.billing.model.Fee;
import com.logopeda.billing.model.Payment;
import com.logopeda.billing.model.SessionBilling;
import com.logopeda.billing.repository.ExportLogRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Builds CSV content for the billing exports. Generation happens entirely on the
 * backend; the frontend only triggers the download. Sensitive clinical notes are
 * intentionally excluded from exports per the privacy guidelines.
 */
@Service
public class CsvExportService {

    private static final String SEPARATOR = ",";
    private static final String NEW_LINE = "\n";

    private final PaymentService paymentService;
    private final SessionBillingService sessionBillingService;
    private final FeeService feeService;
    private final BillingSummaryService summaryService;
    private final ExportLogRepository exportLogRepository;

    public CsvExportService(PaymentService paymentService,
                            SessionBillingService sessionBillingService,
                            FeeService feeService,
                            BillingSummaryService summaryService,
                            ExportLogRepository exportLogRepository) {
        this.paymentService = paymentService;
        this.sessionBillingService = sessionBillingService;
        this.feeService = feeService;
        this.summaryService = summaryService;
        this.exportLogRepository = exportLogRepository;
    }

    public String paymentsCsv(String clinicId, String patientId, LocalDate fromDate, LocalDate toDate) {
        List<Payment> payments = paymentService.search(clinicId, patientId, fromDate, toDate, null, null);
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(SEPARATOR,
                "payment_id", "patient_id", "session_id", "fee_id", "amount",
                "currency", "method", "status", "payment_date", "notes")).append(NEW_LINE);
        for (Payment p : payments) {
            sb.append(row(
                    p.getId(),
                    p.getPatientId(),
                    p.getSessionId(),
                    p.getFeeId(),
                    str(p.getAmount()),
                    p.getCurrency(),
                    p.getMethod().name(),
                    p.getStatus().name(),
                    str(p.getPaymentDate()),
                    p.getNotes()));
        }
        log(clinicId, ExportType.PAYMENTS, "payments.csv");
        return sb.toString();
    }

    public String pendingSessionsCsv(String clinicId, String patientId, LocalDate fromDate, LocalDate toDate) {
        List<SessionBilling> sessions =
                sessionBillingService.search(clinicId, patientId, fromDate, toDate, null).stream()
                        .filter(s -> s.getStatus() == SessionBillingStatus.PENDING
                                || s.getStatus() == SessionBillingStatus.PARTIALLY_PAID)
                        .toList();
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(SEPARATOR,
                "session_billing_id", "patient_id", "session_id", "session_date", "amount",
                "paid_amount", "pending_amount", "status", "notes")).append(NEW_LINE);
        for (SessionBilling s : sessions) {
            BigDecimal paid = s.getPaidAmount() != null ? s.getPaidAmount() : BigDecimal.ZERO;
            BigDecimal pending = s.getAmount().subtract(paid).max(BigDecimal.ZERO);
            sb.append(row(
                    s.getId(),
                    s.getPatientId(),
                    s.getSessionId(),
                    str(s.getSessionDate()),
                    str(s.getAmount()),
                    str(paid),
                    str(pending),
                    s.getStatus().name(),
                    s.getNotes()));
        }
        log(clinicId, ExportType.PENDING_SESSIONS, "pending-sessions.csv");
        return sb.toString();
    }

    public String feesCsv(String clinicId, String patientId, Boolean active) {
        List<Fee> fees = feeService.search(clinicId, patientId, active);
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(SEPARATOR,
                "fee_id", "patient_id", "name", "amount", "currency",
                "recurrence_type", "start_date", "end_date", "active")).append(NEW_LINE);
        for (Fee f : fees) {
            sb.append(row(
                    f.getId(),
                    f.getPatientId(),
                    f.getName(),
                    str(f.getAmount()),
                    f.getCurrency(),
                    f.getRecurrenceType().name(),
                    str(f.getStartDate()),
                    str(f.getEndDate()),
                    String.valueOf(f.isActive())));
        }
        log(clinicId, ExportType.FEES, "fees.csv");
        return sb.toString();
    }

    public String billingSummaryCsv(String clinicId, LocalDate fromDate, LocalDate toDate) {
        BillingSummaryResponse summary = summaryService.buildSummary(clinicId, fromDate, toDate);
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(SEPARATOR,
                "from_date", "to_date", "total_paid", "total_pending", "total_refunded",
                "paid_sessions", "pending_sessions", "active_fees", "currency")).append(NEW_LINE);
        sb.append(row(
                str(fromDate),
                str(toDate),
                str(summary.totalPaid()),
                str(summary.totalPending()),
                str(summary.totalRefunded()),
                String.valueOf(summary.numberOfPaidSessions()),
                String.valueOf(summary.numberOfPendingSessions()),
                String.valueOf(summary.activeFees()),
                summary.currency()));
        log(clinicId, ExportType.SUMMARY, "billing-summary.csv");
        return sb.toString();
    }

    private void log(String clinicId, ExportType type, String fileName) {
        ExportLog logEntry = new ExportLog();
        logEntry.setClinicId(clinicId);
        logEntry.setExportType(type);
        logEntry.setFileName(fileName);
        exportLogRepository.save(logEntry);
    }

    private String row(String... values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(SEPARATOR);
            }
            sb.append(escape(values[i]));
        }
        return sb.append(NEW_LINE).toString();
    }

    private String str(Object value) {
        return value == null ? "" : value.toString();
    }

    /** Minimal RFC-4180 style escaping for CSV field values. */
    private String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean needsQuotes = value.contains(SEPARATOR) || value.contains("\"")
                || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return needsQuotes ? "\"" + escaped + "\"" : escaped;
    }
}
