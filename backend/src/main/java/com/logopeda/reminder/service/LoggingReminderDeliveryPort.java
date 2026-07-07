package com.logopeda.reminder.service;

import com.logopeda.reminder.model.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Default (mock) reminder delivery: logs the reminder and reports success. Used
 * for local development and tests. Active unless {@code app.reminders.delivery}
 * is set to {@code disabled}.
 */
@Component
@ConditionalOnProperty(prefix = "app.reminders", name = "delivery", havingValue = "logging", matchIfMissing = true)
public class LoggingReminderDeliveryPort implements ReminderDeliveryPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingReminderDeliveryPort.class);

    @Override
    public boolean deliver(Reminder reminder) {
        log.info("[reminder] clinic={} target={} title=\"{}\" at={}",
                reminder.getClinicId(), reminder.getTargetUserId(), reminder.getTitle(),
                reminder.getRemindAt());
        return true;
    }
}
