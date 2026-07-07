package com.logopeda.calendar.service;

import com.logopeda.appointment.model.Appointment;
import com.logopeda.appointment.service.AppointmentService;
import com.logopeda.audit.service.AuditService;
import com.logopeda.calendar.dto.CalendarStatusResponse;
import com.logopeda.calendar.dto.CalendarSyncResult;
import com.logopeda.shared.security.TenantContext;
import org.springframework.stereotype.Service;

/**
 * Application service for calendar integration. It resolves appointments in a
 * tenant-scoped way, exposes .ics export (always available) and delegates
 * external sync to the active {@link CalendarProviderPort}.
 */
@Service
public class CalendarService {

    private final CalendarProviderPort provider;
    private final AppointmentService appointmentService;
    private final IcsService icsService;
    private final TenantContext tenantContext;
    private final AuditService auditService;

    public CalendarService(CalendarProviderPort provider, AppointmentService appointmentService,
                           IcsService icsService, TenantContext tenantContext,
                           AuditService auditService) {
        this.provider = provider;
        this.appointmentService = appointmentService;
        this.icsService = icsService;
        this.tenantContext = tenantContext;
        this.auditService = auditService;
    }

    public CalendarStatusResponse status() {
        return new CalendarStatusResponse(provider.provider(), provider.isEnabled());
    }

    public String exportIcs(String appointmentId) {
        String clinicId = tenantContext.requireClinicId();
        Appointment appointment = appointmentService.getById(clinicId, appointmentId);
        auditService.record("APPOINTMENT_ICS_EXPORTED", "Appointment", appointmentId, null);
        return icsService.buildIcs(appointment);
    }

    public CalendarSyncResult sync(String appointmentId) {
        String clinicId = tenantContext.requireClinicId();
        Appointment appointment = appointmentService.getById(clinicId, appointmentId);
        CalendarSyncResult result = provider.pushAppointment(appointment);
        auditService.record("APPOINTMENT_CALENDAR_SYNCED", "Appointment", appointmentId,
                "provider=" + result.provider() + ";synced=" + result.synced());
        return result;
    }
}
