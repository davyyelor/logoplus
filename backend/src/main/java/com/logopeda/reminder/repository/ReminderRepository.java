package com.logopeda.reminder.repository;

import com.logopeda.reminder.enums.ReminderStatus;
import com.logopeda.reminder.model.Reminder;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, String> {

    Optional<Reminder> findByIdAndClinicId(String id, String clinicId);

    List<Reminder> findByClinicIdOrderByRemindAtDesc(String clinicId);

    List<Reminder> findByClinicIdAndTargetUserIdOrderByRemindAtDesc(String clinicId, String targetUserId);

    List<Reminder> findByTargetUserIdOrderByRemindAtDesc(String targetUserId);

    /** System-wide query used by the scheduler (no tenant scoping by design). */
    List<Reminder> findTop100ByStatusAndRemindAtLessThanEqualOrderByRemindAtAsc(
            ReminderStatus status, Instant threshold);
}
