package com.logopeda.calendar.service;

import com.logopeda.appointment.model.Appointment;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** Builds RFC 5545 iCalendar (.ics) documents for appointments. */
@Component
public class IcsService {

    private static final DateTimeFormatter UTC =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);

    public String buildIcs(Appointment appointment) {
        String uid = appointment.getId() + "@logoplus";
        String summary = escape(StringUtils.hasText(appointment.getTitle())
                ? appointment.getTitle() : "Cita");
        String description = escape(appointment.getNotes());
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCALENDAR\r\n");
        sb.append("VERSION:2.0\r\n");
        sb.append("PRODID:-//LogoPlus//Appointments//ES\r\n");
        sb.append("CALSCALE:GREGORIAN\r\n");
        sb.append("METHOD:PUBLISH\r\n");
        sb.append("BEGIN:VEVENT\r\n");
        sb.append("UID:").append(uid).append("\r\n");
        sb.append("DTSTAMP:").append(UTC.format(Instant.now())).append("\r\n");
        sb.append("DTSTART:").append(UTC.format(appointment.getStartDateTime())).append("\r\n");
        sb.append("DTEND:").append(UTC.format(appointment.getEndDateTime())).append("\r\n");
        sb.append("SUMMARY:").append(summary).append("\r\n");
        if (StringUtils.hasText(description)) {
            sb.append("DESCRIPTION:").append(description).append("\r\n");
        }
        sb.append("END:VEVENT\r\n");
        sb.append("END:VCALENDAR\r\n");
        return sb.toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\r\n", "\\n")
                .replace("\n", "\\n");
    }
}
