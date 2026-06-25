package com.logopeda.billing.mapper;

import com.logopeda.billing.dto.SessionBillingResponse;
import com.logopeda.billing.model.SessionBilling;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class SessionBillingMapper {

    public SessionBillingResponse toResponse(SessionBilling s) {
        BigDecimal paid = s.getPaidAmount() != null ? s.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal pending = s.getAmount().subtract(paid).max(BigDecimal.ZERO);
        return new SessionBillingResponse(
                s.getId(),
                s.getClinicId(),
                s.getPatientId(),
                s.getSessionId(),
                s.getSessionDate(),
                s.getAmount(),
                s.getCurrency(),
                s.getStatus(),
                paid,
                pending,
                s.getPaymentId(),
                s.getNotes(),
                s.getCreatedAt(),
                s.getUpdatedAt());
    }
}
