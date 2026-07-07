package com.logopeda.reminder.controller;

import com.logopeda.reminder.dto.ReminderRequest;
import com.logopeda.reminder.dto.ReminderResponse;
import com.logopeda.reminder.mapper.ReminderMapper;
import com.logopeda.reminder.service.ReminderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal reminder management for clinical staff. Reads are open to RECEPTION;
 * mutations are limited to CLINIC_ADMIN and THERAPIST.
 */
@RestController
public class ReminderController {

    private final ReminderService reminderService;
    private final ReminderMapper reminderMapper;

    public ReminderController(ReminderService reminderService, ReminderMapper reminderMapper) {
        this.reminderService = reminderService;
        this.reminderMapper = reminderMapper;
    }

    @GetMapping("/api/reminders")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public List<ReminderResponse> list() {
        return reminderService.list().stream().map(reminderMapper::toResponse).toList();
    }

    @GetMapping("/api/reminders/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public ReminderResponse get(@PathVariable String id) {
        return reminderMapper.toResponse(reminderService.getById(id));
    }

    @PostMapping("/api/reminders")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderResponse create(@Valid @RequestBody ReminderRequest request) {
        return reminderMapper.toResponse(reminderService.create(request));
    }

    @PutMapping("/api/reminders/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public ReminderResponse update(@PathVariable String id, @Valid @RequestBody ReminderRequest request) {
        return reminderMapper.toResponse(reminderService.update(id, request));
    }

    @PatchMapping("/api/reminders/{id}/cancel")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public ReminderResponse cancel(@PathVariable String id) {
        return reminderMapper.toResponse(reminderService.cancel(id));
    }

    @DeleteMapping("/api/reminders/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        reminderService.delete(id);
    }
}
