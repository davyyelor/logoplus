package com.logopeda.auth.service;

import com.logopeda.auth.dto.AuthResponse;
import com.logopeda.auth.dto.LoginRequest;
import com.logopeda.auth.dto.RegisterClinicAdminRequest;
import com.logopeda.clinic.dto.ClinicRequest;
import com.logopeda.clinic.model.Clinic;
import com.logopeda.clinic.service.ClinicService;
import com.logopeda.shared.security.JwtService;
import com.logopeda.shared.security.UnauthorizedException;
import com.logopeda.user.model.User;
import com.logopeda.user.repository.UserRepository;
import com.logopeda.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication use cases: login and self-service clinic registration. Tokens
 * are stateless JWTs issued by {@link JwtService}.
 */
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ClinicService clinicService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, UserService userService,
                       ClinicService clinicService, PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.clinicService = clinicService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        return buildAuthResponse(user);
    }

    public AuthResponse registerClinicAdmin(RegisterClinicAdminRequest request) {
        Clinic clinic = clinicService.create(new ClinicRequest(
                request.clinicName(), null, null, request.email(),
                null, null, null, null, null, "ES"));
        User admin = userService.createClinicAdmin(
                clinic.getId(), request.email(), request.password(),
                request.firstName(), request.lastName());
        return buildAuthResponse(admin);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(
                user.getId(), user.getClinicId(), user.getEmail(), user.getRole());
        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMinutes(),
                user.getId(),
                user.getClinicId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole());
    }
}
