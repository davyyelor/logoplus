package com.logopeda.reminder.service;

import com.logopeda.reminder.model.Reminder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Disabled reminder delivery: performs no external action and reports success so
 * reminders are simply marked SENT without being pushed anywhere. Active when
 * {@code app.reminders.delivery=disabled}.
 */
@Component
@ConditionalOnProperty(prefix = "app.reminders", name = "delivery", havingValue = "disabled")
public class DisabledReminderDeliveryPort implements ReminderDeliveryPort {

    @Override
    public boolean deliver(Reminder reminder) {
        return true;
    }
}
