package com.logopeda.billing.mapper;

import com.logopeda.billing.dto.PaymentResponse;
import com.logopeda.billing.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getClinicId(),
                p.getPatientId(),
                p.getSessionId(),
                p.getFeeId(),
                p.getAmount(),
                p.getCurrency(),
                p.getPaymentDate(),
                p.getMethod(),
                p.getStatus(),
                p.getNotes(),
                p.getCreatedAt(),
                p.getUpdatedAt());
    }
}
