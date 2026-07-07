package com.logopeda.calendar.service;

import com.logopeda.appointment.model.Appointment;
import com.logopeda.calendar.config.CalendarProperties;
import com.logopeda.calendar.dto.CalendarSyncResult;
import com.logopeda.calendar.enums.CalendarProviderType;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Outlook Calendar provider. When client credentials are present it operates
 * in a mock-sync mode (no live OAuth flow is bundled in this build); otherwise
 * it reports itself as not configured.
 */
@Component
@ConditionalOnProperty(prefix = "app.calendar", name = "provider", havingValue = "outlook")
public class OutlookCalendarProvider implements CalendarProviderPort {

    private final CalendarProperties properties;

    public OutlookCalendarProvider(CalendarProperties properties) {
        this.properties = properties;
    }

    @Override
    public CalendarProviderType provider() {
        return CalendarProviderType.OUTLOOK;
    }

    @Override
    public boolean isEnabled() {
        return StringUtils.hasText(properties.getOutlookClientId());
    }

    @Override
    public CalendarSyncResult pushAppointment(Appointment appointment) {
        if (!isEnabled()) {
            return new CalendarSyncResult(CalendarProviderType.OUTLOOK, false, null,
                    "Outlook Calendar is not configured");
        }
        String eventId = "outlook_mock_" + UUID.randomUUID().toString().replace("-", "");
        return new CalendarSyncResult(CalendarProviderType.OUTLOOK, true, eventId,
                "Appointment queued for Outlook Calendar (mock)");
    }
}
