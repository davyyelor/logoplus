package com.logopeda.user.controller;

import com.logopeda.shared.security.TenantContext;
import com.logopeda.user.dto.CreateUserRequest;
import com.logopeda.user.dto.UpdateUserRequest;
import com.logopeda.user.dto.UserResponse;
import com.logopeda.user.mapper.UserMapper;
import com.logopeda.user.service.UserService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Clinic-admin user management. All endpoints scoped to the caller's clinic. */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('CLINIC_ADMIN')")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final TenantContext tenantContext;

    public UserController(UserService userService, UserMapper userMapper, TenantContext tenantContext) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.tenantContext = tenantContext;
    }

    @GetMapping
    public List<UserResponse> list() {
        return userService.list(tenantContext.requireClinicId())
                .stream().map(userMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable String id) {
        return userMapper.toResponse(userService.getById(tenantContext.requireClinicId(), id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userMapper.toResponse(userService.create(tenantContext.requireClinicId(), request));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable String id, @Valid @RequestBody UpdateUserRequest request) {
        return userMapper.toResponse(userService.update(tenantContext.requireClinicId(), id, request));
    }

    @PatchMapping("/{id}/activate")
    public UserResponse activate(@PathVariable String id) {
        return userMapper.toResponse(userService.setActive(tenantContext.requireClinicId(), id, true));
    }

    @PatchMapping("/{id}/deactivate")
    public UserResponse deactivate(@PathVariable String id) {
        return userMapper.toResponse(userService.setActive(tenantContext.requireClinicId(), id, false));
    }
}
