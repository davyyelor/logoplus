package com.logopeda.homework.controller;

import com.logopeda.homework.dto.HomeworkRequest;
import com.logopeda.homework.dto.HomeworkResponse;
import com.logopeda.homework.dto.UpdateHomeworkStatusRequest;
import com.logopeda.homework.mapper.HomeworkMapper;
import com.logopeda.homework.service.HomeworkService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Home task management for clinical staff. Reads are open to RECEPTION as well;
 * mutations are limited to CLINIC_ADMIN and THERAPIST.
 */
@RestController
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final HomeworkMapper homeworkMapper;

    public HomeworkController(HomeworkService homeworkService, HomeworkMapper homeworkMapper) {
        this.homeworkService = homeworkService;
        this.homeworkMapper = homeworkMapper;
    }

    @GetMapping("/api/patients/{patientId}/homework")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public List<HomeworkResponse> listForPatient(@PathVariable String patientId) {
        return homeworkService.listForPatient(patientId).stream().map(homeworkMapper::toResponse).toList();
    }

    @GetMapping("/api/homework/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public HomeworkResponse get(@PathVariable String id) {
        return homeworkMapper.toResponse(homeworkService.getById(id));
    }

    @PostMapping("/api/patients/{patientId}/homework")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    @ResponseStatus(HttpStatus.CREATED)
    public HomeworkResponse create(@PathVariable String patientId,
                                   @Valid @RequestBody HomeworkRequest request) {
        return homeworkMapper.toResponse(homeworkService.create(patientId, request));
    }

    @PutMapping("/api/homework/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public HomeworkResponse update(@PathVariable String id,
                                   @Valid @RequestBody HomeworkRequest request) {
        return homeworkMapper.toResponse(homeworkService.update(id, request));
    }

    @PatchMapping("/api/homework/{id}/status")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    public HomeworkResponse changeStatus(@PathVariable String id,
                                         @Valid @RequestBody UpdateHomeworkStatusRequest request) {
        return homeworkMapper.toResponse(homeworkService.changeStatus(id, request.status()));
    }

    @DeleteMapping("/api/homework/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        homeworkService.delete(id);
    }
}
