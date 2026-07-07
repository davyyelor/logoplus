package com.logopeda.calendar.service;

import com.logopeda.appointment.model.Appointment;
import com.logopeda.calendar.dto.CalendarSyncResult;
import com.logopeda.calendar.enums.CalendarProviderType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Default provider used when calendar sync is disabled. It never contacts an
 * external calendar; the .ics export remains available independently.
 */
@Component
@ConditionalOnProperty(prefix = "app.calendar", name = "provider", havingValue = "disabled",
        matchIfMissing = true)
public class DisabledCalendarProvider implements CalendarProviderPort {

    @Override
    public CalendarProviderType provider() {
        return CalendarProviderType.DISABLED;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public CalendarSyncResult pushAppointment(Appointment appointment) {
        return new CalendarSyncResult(CalendarProviderType.DISABLED, false, null,
                "Calendar sync is disabled");
    }
}
