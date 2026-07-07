package com.logopeda.reminder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Polls for due reminders and dispatches them. The interval is configurable via
 * {@code app.reminders.poll-interval-ms} (default 60s). The whole feature can be
 * turned off with {@code app.reminders.scheduler-enabled=false}.
 */
@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final ReminderService reminderService;

    public ReminderScheduler(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Scheduled(
            initialDelayString = "${app.reminders.initial-delay-ms:30000}",
            fixedDelayString = "${app.reminders.poll-interval-ms:60000}")
    public void dispatchDueReminders() {
        try {
            int delivered = reminderService.dispatchDue();
            if (delivered > 0) {
                log.debug("Dispatched {} due reminder(s)", delivered);
            }
        } catch (RuntimeException ex) {
            log.warn("Reminder dispatch cycle failed: {}", ex.getMessage());
        }
    }
}
