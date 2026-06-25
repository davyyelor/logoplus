package com.logopeda.document.controller;

import com.logopeda.document.dto.DocumentContent;
import com.logopeda.document.dto.DocumentResponse;
import com.logopeda.document.enums.DocumentType;
import com.logopeda.document.mapper.DocumentMapper;
import com.logopeda.document.service.DocumentService;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Staff-facing document management (upload, download, share, delete). */
@RestController
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentMapper documentMapper;

    public DocumentController(DocumentService documentService, DocumentMapper documentMapper) {
        this.documentService = documentService;
        this.documentMapper = documentMapper;
    }

    @GetMapping("/api/patients/{patientId}/documents")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public List<DocumentResponse> listForPatient(@PathVariable String patientId) {
        return documentService.listForPatient(patientId)
                .stream().map(documentMapper::toResponse).toList();
    }

    @PostMapping("/api/patients/{patientId}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public DocumentResponse upload(@PathVariable String patientId,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam(value = "documentType", required = false) DocumentType documentType,
                                   @RequestParam(value = "visibleToFamily", defaultValue = "false") boolean visibleToFamily) {
        return documentMapper.toResponse(documentService.upload(patientId, file, documentType, visibleToFamily));
    }

    @GetMapping("/api/documents/{id}/download")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        DocumentContent content = documentService.download(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + content.fileName() + "\"")
                .contentType(MediaType.parseMediaType(content.contentType()))
                .body(content.content());
    }

    @PatchMapping("/api/documents/{id}/share-with-family")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public DocumentResponse shareWithFamily(@PathVariable String id) {
        return documentMapper.toResponse(documentService.setVisibleToFamily(id, true));
    }

    @PatchMapping("/api/documents/{id}/hide-from-family")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public DocumentResponse hideFromFamily(@PathVariable String id) {
        return documentMapper.toResponse(documentService.setVisibleToFamily(id, false));
    }

    @DeleteMapping("/api/documents/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public void delete(@PathVariable String id) {
        documentService.delete(id);
    }
}
