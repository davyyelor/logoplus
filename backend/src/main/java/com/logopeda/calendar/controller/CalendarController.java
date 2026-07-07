package com.logopeda.calendar.controller;

import com.logopeda.calendar.dto.CalendarStatusResponse;
import com.logopeda.calendar.dto.CalendarSyncResult;
import com.logopeda.calendar.service.CalendarService;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** Calendar integration endpoints: status, .ics export and external sync. */
@RestController
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/api/calendar/status")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public CalendarStatusResponse status() {
        return calendarService.status();
    }

    @GetMapping("/api/appointments/{id}/calendar.ics")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public ResponseEntity<byte[]> exportIcs(@PathVariable String id) {
        byte[] body = calendarService.exportIcs(id).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cita-" + id + ".ics")
                .contentType(MediaType.parseMediaType("text/calendar"))
                .body(body);
    }

    @PostMapping("/api/appointments/{id}/calendar/sync")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public CalendarSyncResult sync(@PathVariable String id) {
        return calendarService.sync(id);
    }
}
