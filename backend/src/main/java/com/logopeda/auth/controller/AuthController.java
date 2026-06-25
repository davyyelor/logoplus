package com.logopeda.auth.controller;

import com.logopeda.auth.dto.AuthResponse;
import com.logopeda.auth.dto.CurrentUserResponse;
import com.logopeda.auth.dto.LoginRequest;
import com.logopeda.auth.dto.RegisterClinicAdminRequest;
import com.logopeda.auth.service.AuthService;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.shared.security.AuthenticatedUser;
import com.logopeda.shared.security.TenantContext;
import com.logopeda.user.model.User;
import com.logopeda.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TenantContext tenantContext;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, TenantContext tenantContext,
                          UserRepository userRepository) {
        this.authService = authService;
        this.tenantContext = tenantContext;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register-clinic-admin")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registerClinicAdmin(@Valid @RequestBody RegisterClinicAdminRequest request) {
        return authService.registerClinicAdmin(request);
    }

    @GetMapping("/me")
    public CurrentUserResponse me() {
        AuthenticatedUser principal = tenantContext.requireCurrentUser();
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getUserId()));
        return new CurrentUserResponse(
                user.getId(),
                user.getClinicId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole());
    }
}
