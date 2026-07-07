package com.logopeda.calendar.service;

import com.logopeda.appointment.model.Appointment;
import com.logopeda.calendar.dto.CalendarSyncResult;
import com.logopeda.calendar.enums.CalendarProviderType;

/**
 * Port for external calendar synchronisation. Implementations are selected via
 * {@code app.calendar.provider}. External-integration behaviour lives behind
 * this interface so the application can run fully with calendar sync disabled.
 */
public interface CalendarProviderPort {

    CalendarProviderType provider();

    boolean isEnabled();

    CalendarSyncResult pushAppointment(Appointment appointment);
}
