package com.logopeda.signature.mapper;

import com.logopeda.signature.dto.SignatureResponse;
import com.logopeda.signature.model.SignatureRecord;
import org.springframework.stereotype.Component;

@Component
public class SignatureMapper {

    public SignatureResponse toResponse(SignatureRecord record) {
        return new SignatureResponse(
                record.getId(),
                record.getClinicId(),
                record.getPatientId(),
                record.getDocumentType(),
                record.getDocumentId(),
                record.getSignerUserId(),
                record.getSignerName(),
                record.getProvider(),
                record.getStatus(),
                record.getSignatureHash(),
                record.getNote(),
                record.getSignedAt(),
                record.getCreatedAt(),
                record.getUpdatedAt());
    }
}
