package com.logopeda.reminder.service;

import com.logopeda.audit.service.AuditService;
import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.patient.service.PatientAccessGuard;
import com.logopeda.reminder.dto.ReminderRequest;
import com.logopeda.reminder.enums.ReminderStatus;
import com.logopeda.reminder.model.Reminder;
import com.logopeda.reminder.repository.ReminderRepository;
import com.logopeda.shared.security.TenantContext;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Internal reminders. Staff create/manage reminders scoped to their clinic;
 * family users may read the reminders addressed to them. A scheduled job
 * ({@link ReminderScheduler}) dispatches due reminders through the configured
 * {@link ReminderDeliveryPort}. No clinical automation is performed — reminders
 * are simple, staff-authored notes with a delivery time.
 */
@Service
@Transactional
public class ReminderService {

    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);
    private static final String ENTITY = "Reminder";

    private final ReminderRepository reminderRepository;
    private final ReminderDeliveryPort deliveryPort;
    private final PatientAccessGuard patientAccessGuard;
    private final TenantContext tenantContext;
    private final AuditService auditService;

    public ReminderService(ReminderRepository reminderRepository, ReminderDeliveryPort deliveryPort,
                           PatientAccessGuard patientAccessGuard, TenantContext tenantContext,
                           AuditService auditService) {
        this.reminderRepository = reminderRepository;
        this.deliveryPort = deliveryPort;
        this.patientAccessGuard = patientAccessGuard;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<Reminder> list() {
        return reminderRepository.findByClinicIdOrderByRemindAtDesc(tenantContext.requireClinicId());
    }

    @Transactional(readOnly = true)
    public Reminder getById(String id) {
        return findOwned(id);
    }

    public Reminder create(ReminderRequest request) {
        Reminder reminder = new Reminder();
        reminder.setClinicId(tenantContext.requireClinicId());
        reminder.setCreatedByUserId(tenantContext.currentUserId());
        reminder.setStatus(ReminderStatus.SCHEDULED);
        apply(reminder, request);
        Reminder saved = reminderRepository.save(reminder);
        auditService.record("REMINDER_CREATED", ENTITY, saved.getId(), null);
        return saved;
    }

    public Reminder update(String id, ReminderRequest request) {
        Reminder reminder = findOwned(id);
        if (reminder.getStatus() == ReminderStatus.SENT) {
            throw new BusinessValidationException("A sent reminder cannot be modified");
        }
        apply(reminder, request);
        return reminderRepository.save(reminder);
    }

    public Reminder cancel(String id) {
        Reminder reminder = findOwned(id);
        reminder.setStatus(ReminderStatus.CANCELLED);
        Reminder saved = reminderRepository.save(reminder);
        auditService.record("REMINDER_CANCELLED", ENTITY, saved.getId(), null);
        return saved;
    }

    public void delete(String id) {
        reminderRepository.delete(findOwned(id));
    }

    /** Reminders addressed to the current family user. */
    @Transactional(readOnly = true)
    public List<Reminder> listForCurrentUser() {
        return reminderRepository.findByClinicIdAndTargetUserIdOrderByRemindAtDesc(
                tenantContext.requireClinicId(), tenantContext.currentUserId());
    }

    /**
     * Dispatches all due, still-scheduled reminders. Invoked by the scheduler; runs
     * without tenant context by design (system job).
     */
    public int dispatchDue() {
        List<Reminder> due = reminderRepository
                .findTop100ByStatusAndRemindAtLessThanEqualOrderByRemindAtAsc(
                        ReminderStatus.SCHEDULED, Instant.now());
        int delivered = 0;
        for (Reminder reminder : due) {
            try {
                boolean ok = deliveryPort.deliver(reminder);
                reminder.setStatus(ok ? ReminderStatus.SENT : ReminderStatus.FAILED);
                if (ok) {
                    reminder.setSentAt(Instant.now());
                    delivered++;
                }
            } catch (RuntimeException ex) {
                log.warn("Reminder delivery failed for {}: {}", reminder.getId(), ex.getMessage());
                reminder.setStatus(ReminderStatus.FAILED);
            }
            reminderRepository.save(reminder);
        }
        return delivered;
    }

    private void apply(Reminder reminder, ReminderRequest request) {
        if (request.title() == null || request.title().isBlank()) {
            throw new BusinessValidationException("Title is required");
        }
        if (request.remindAt() == null) {
            throw new BusinessValidationException("Reminder time is required");
        }
        // If the reminder targets a patient, enforce access rules.
        if (request.patientId() != null && !request.patientId().isBlank()) {
            patientAccessGuard.requireAccessiblePatient(request.patientId());
            reminder.setPatientId(request.patientId());
        }
        reminder.setTargetUserId(request.targetUserId());
        reminder.setTitle(request.title().trim());
        reminder.setMessage(request.message());
        reminder.setRemindAt(request.remindAt());
        reminder.setRelatedEntityType(request.relatedEntityType());
        reminder.setRelatedEntityId(request.relatedEntityId());
    }

    private Reminder findOwned(String id) {
        return reminderRepository.findByIdAndClinicId(id, tenantContext.requireClinicId())
                .orElseThrow(() -> new ResourceNotFoundException(ENTITY, id));
    }
}
