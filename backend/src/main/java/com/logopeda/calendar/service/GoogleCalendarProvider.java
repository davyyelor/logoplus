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
 * Google Calendar provider. When client credentials are present it operates in
 * a mock-sync mode (no live OAuth flow is bundled in this build); otherwise it
 * reports itself as not configured.
 */
@Component
@ConditionalOnProperty(prefix = "app.calendar", name = "provider", havingValue = "google")
public class GoogleCalendarProvider implements CalendarProviderPort {

    private final CalendarProperties properties;

    public GoogleCalendarProvider(CalendarProperties properties) {
        this.properties = properties;
    }

    @Override
    public CalendarProviderType provider() {
        return CalendarProviderType.GOOGLE;
    }

    @Override
    public boolean isEnabled() {
        return StringUtils.hasText(properties.getGoogleClientId());
    }

    @Override
    public CalendarSyncResult pushAppointment(Appointment appointment) {
        if (!isEnabled()) {
            return new CalendarSyncResult(CalendarProviderType.GOOGLE, false, null,
                    "Google Calendar is not configured");
        }
        String eventId = "google_mock_" + UUID.randomUUID().toString().replace("-", "");
        return new CalendarSyncResult(CalendarProviderType.GOOGLE, true, eventId,
                "Appointment queued for Google Calendar (mock)");
    }
}
