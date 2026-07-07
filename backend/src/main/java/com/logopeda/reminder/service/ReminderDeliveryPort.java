package com.logopeda.reminder.service;

import com.logopeda.reminder.model.Reminder;

/**
 * Port for delivering an internal reminder to its recipient. External delivery
 * channels (email, push, ...) must be implemented behind this interface and be
 * able to run in a disabled or mock mode. The core scheduler only depends on
 * this abstraction.
 */
public interface ReminderDeliveryPort {

    /**
     * Attempts to deliver the reminder.
     *
     * @return {@code true} if delivery succeeded (reminder becomes SENT),
     *         {@code false} if it should be retried/failed.
     */
    boolean deliver(Reminder reminder);
}
