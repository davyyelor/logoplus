package com.logopeda.notification.service;

import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.notification.enums.NotificationType;
import com.logopeda.notification.model.Notification;
import com.logopeda.notification.repository.NotificationRepository;
import com.logopeda.notification.sender.EmailSenderPort;
import com.logopeda.notification.sender.PushSenderPort;
import com.logopeda.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * In-app notifications for the current user, plus a creation entry point other
 * modules can call. Email/push delivery is delegated to placeholder ports so the
 * feature is wired end-to-end without external providers in V1.
 */
@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final TenantContext tenantContext;
    private final EmailSenderPort emailSender;
    private final PushSenderPort pushSender;

    public NotificationService(NotificationRepository notificationRepository, TenantContext tenantContext,
                               EmailSenderPort emailSender, PushSenderPort pushSender) {
        this.notificationRepository = notificationRepository;
        this.tenantContext = tenantContext;
        this.emailSender = emailSender;
        this.pushSender = pushSender;
    }

    /** Creates an in-app notification and triggers placeholder email/push delivery. */
    public Notification notifyUser(String clinicId, String userId, NotificationType type,
                                   String title, String message, String reference) {
        return notifyUser(clinicId, userId, null, type, title, message, reference);
    }

    /**
     * Creates an in-app notification and triggers placeholder email/push delivery.
     * When {@code email} is provided, a placeholder email is also "sent".
     */
    public Notification notifyUser(String clinicId, String userId, String email, NotificationType type,
                                   String title, String message, String reference) {
        Notification notification = new Notification();
        notification.setClinicId(clinicId);
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReference(reference);
        notification.setReadFlag(false);
        Notification saved = notificationRepository.save(notification);
        // Placeholder fan-out; real delivery is added with a provider later.
        pushSender.send(userId, title, message);
        if (email != null && !email.isBlank()) {
            emailSender.send(email, title, message);
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Notification> listForCurrentUser() {
        String userId = tenantContext.requireCurrentUser().getUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long unreadCount() {
        String userId = tenantContext.requireCurrentUser().getUserId();
        return notificationRepository.countByUserIdAndReadFlagFalse(userId);
    }

    public Notification markRead(String id) {
        String userId = tenantContext.requireCurrentUser().getUserId();
        Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        notification.setReadFlag(true);
        return notificationRepository.save(notification);
    }

    public void markAllRead() {
        String userId = tenantContext.requireCurrentUser().getUserId();
        notificationRepository.markAllRead(userId);
    }
}
