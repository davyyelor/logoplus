package com.logopeda.center.controller;

import com.logopeda.center.dto.CenterRequest;
import com.logopeda.center.dto.CenterResponse;
import com.logopeda.center.mapper.CenterMapper;
import com.logopeda.center.service.CenterService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Center administration. Any staff role may read the centers of their clinic
 * (needed to populate selectors); only CLINIC_ADMIN may create or modify them.
 */
@RestController
public class CenterController {

    private final CenterService centerService;
    private final CenterMapper centerMapper;

    public CenterController(CenterService centerService, CenterMapper centerMapper) {
        this.centerService = centerService;
        this.centerMapper = centerMapper;
    }

    @GetMapping("/api/centers")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public List<CenterResponse> list() {
        return centerService.list().stream().map(centerMapper::toResponse).toList();
    }

    @GetMapping("/api/centers/{id}")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN','THERAPIST','RECEPTION')")
    public CenterResponse get(@PathVariable String id) {
        return centerMapper.toResponse(centerService.getById(id));
    }

    @PostMapping("/api/centers")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CenterResponse create(@Valid @RequestBody CenterRequest request) {
        return centerMapper.toResponse(centerService.create(request));
    }

    @PutMapping("/api/centers/{id}")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public CenterResponse update(@PathVariable String id, @Valid @RequestBody CenterRequest request) {
        return centerMapper.toResponse(centerService.update(id, request));
    }

    @PatchMapping("/api/centers/{id}/activate")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public CenterResponse activate(@PathVariable String id) {
        return centerMapper.toResponse(centerService.setActive(id, true));
    }

    @PatchMapping("/api/centers/{id}/deactivate")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public CenterResponse deactivate(@PathVariable String id) {
        return centerMapper.toResponse(centerService.setActive(id, false));
    }
}
