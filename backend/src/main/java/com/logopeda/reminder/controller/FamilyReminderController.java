package com.logopeda.reminder.controller;

import com.logopeda.reminder.dto.ReminderResponse;
import com.logopeda.reminder.mapper.ReminderMapper;
import com.logopeda.reminder.service.ReminderService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only reminder access for family users: returns only the reminders
 * addressed to the authenticated family member within their clinic.
 */
@RestController
public class FamilyReminderController {

    private final ReminderService reminderService;
    private final ReminderMapper reminderMapper;

    public FamilyReminderController(ReminderService reminderService, ReminderMapper reminderMapper) {
        this.reminderService = reminderService;
        this.reminderMapper = reminderMapper;
    }

    @GetMapping("/api/family/reminders")
    @PreAuthorize("hasRole('FAMILY')")
    public List<ReminderResponse> myReminders() {
        return reminderService.listForCurrentUser().stream().map(reminderMapper::toResponse).toList();
    }
}
