package com.logopeda.calendar.dto;

import com.logopeda.calendar.enums.CalendarProviderType;

/** Public status of the calendar integration for the clinic UI. */
public record CalendarStatusResponse(
        CalendarProviderType provider,
        boolean enabled) {
}
