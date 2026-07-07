package com.logopeda.homework.controller;

import com.logopeda.homework.dto.HomeworkResponse;
import com.logopeda.homework.dto.UpdateHomeworkStatusRequest;
import com.logopeda.homework.mapper.HomeworkMapper;
import com.logopeda.homework.service.HomeworkService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Family-facing home task endpoints. Restricted to FAMILY and additionally scoped
 * to the patients the guardian is linked to (via the service/access guard).
 * Families read visible tasks and may advance their status.
 */
@RestController
@PreAuthorize("hasRole('FAMILY')")
public class FamilyHomeworkController {

    private final HomeworkService homeworkService;
    private final HomeworkMapper homeworkMapper;

    public FamilyHomeworkController(HomeworkService homeworkService, HomeworkMapper homeworkMapper) {
        this.homeworkService = homeworkService;
        this.homeworkMapper = homeworkMapper;
    }

    @GetMapping("/api/family/patients/{patientId}/homework")
    public List<HomeworkResponse> listForPatient(@PathVariable String patientId) {
        return homeworkService.listForFamily(patientId).stream().map(homeworkMapper::toResponse).toList();
    }

    @PatchMapping("/api/family/homework/{id}/status")
    public HomeworkResponse changeStatus(@PathVariable String id,
                                         @Valid @RequestBody UpdateHomeworkStatusRequest request) {
        return homeworkMapper.toResponse(homeworkService.changeStatusAsFamily(id, request.status()));
    }
}
