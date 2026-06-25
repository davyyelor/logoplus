package com.logopeda.document.mapper;

import com.logopeda.document.dto.DocumentResponse;
import com.logopeda.document.model.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getClinicId(),
                document.getPatientId(),
                document.getDocumentType(),
                document.getOriginalFileName(),
                document.getContentType(),
                document.getSizeBytes(),
                document.isVisibleToFamily(),
                document.getUploadedByUserId(),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }
}
