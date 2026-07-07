package com.logopeda.calendar.dto;

import com.logopeda.calendar.enums.CalendarProviderType;

/** Outcome of pushing an appointment to an external calendar provider. */
public record CalendarSyncResult(
        CalendarProviderType provider,
        boolean synced,
        String externalEventId,
        String message) {
}
