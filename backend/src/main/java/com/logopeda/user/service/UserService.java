package com.logopeda.user.service;

import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.billing.exception.ResourceNotFoundException;
import com.logopeda.shared.enums.Role;
import com.logopeda.user.dto.CreateUserRequest;
import com.logopeda.user.dto.UpdateUserRequest;
import com.logopeda.user.model.User;
import com.logopeda.user.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * User management for clinic administrators. All operations are scoped to a
 * clinic; cross-clinic access is impossible because reads/writes always go
 * through {@code clinicId}.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.logopeda.center.service.CenterService centerService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       com.logopeda.center.service.CenterService centerService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.centerService = centerService;
    }

    @Transactional(readOnly = true)
    public List<User> list(String clinicId) {
        return userRepository.findByClinicIdOrderByCreatedAtDesc(clinicId);
    }

    @Transactional(readOnly = true)
    public User getById(String clinicId, String id) {
        return findOwned(clinicId, id);
    }

    public User create(String clinicId, CreateUserRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessValidationException("A user with this email already exists");
        }
        User user = new User();
        user.setClinicId(clinicId);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setRole(request.role());
        user.setActive(true);
        String centerId = StringUtils.hasText(request.centerId()) ? request.centerId().trim() : null;
        centerService.requireCenterInClinic(centerId);
        user.setCenterId(centerId);
        return userRepository.save(user);
    }

    public User update(String clinicId, String id, UpdateUserRequest request) {
        User user = findOwned(clinicId, id);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setRole(request.role());
        if (StringUtils.hasText(request.password())) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        String centerId = StringUtils.hasText(request.centerId()) ? request.centerId().trim() : null;
        centerService.requireCenterInClinic(centerId);
        user.setCenterId(centerId);
        return userRepository.save(user);
    }

    public User setActive(String clinicId, String id, boolean active) {
        User user = findOwned(clinicId, id);
        user.setActive(active);
        return userRepository.save(user);
    }

    /**
     * Creates the very first clinic administrator. Used during clinic
     * registration only; email must be globally unique.
     */
    public User createClinicAdmin(String clinicId, String email, String rawPassword,
                                  String firstName, String lastName) {
        String normalized = email.trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalized)) {
            throw new BusinessValidationException("A user with this email already exists");
        }
        User user = new User();
        user.setClinicId(clinicId);
        user.setEmail(normalized);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setRole(Role.CLINIC_ADMIN);
        user.setActive(true);
        return userRepository.save(user);
    }

    /**
     * Creates a FAMILY portal user for a guardian. Returns the created user so
     * the caller can link the guardian to it.
     */
    public User createFamilyUser(String clinicId, String email, String rawPassword,
                                 String firstName, String lastName) {
        String normalized = email.trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalized)) {
            throw new BusinessValidationException("A user with this email already exists");
        }
        User user = new User();
        user.setClinicId(clinicId);
        user.setEmail(normalized);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setRole(Role.FAMILY);
        user.setActive(true);
        return userRepository.save(user);
    }

    private User findOwned(String clinicId, String id) {
        return userRepository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
