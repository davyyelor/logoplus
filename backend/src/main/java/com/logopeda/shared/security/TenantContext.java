package com.logopeda.shared.security;

import com.logopeda.shared.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Single seam for reading the current tenant/user. Every V1 service resolves the
 * clinic and the acting user through this component instead of touching the
 * Spring Security context directly, so isolation rules live in one place.
 *
 * <p>When no authenticated user is present (e.g. the legacy billing endpoints
 * before a token exists), it falls back to the configured demo clinic so the
 * app keeps working in local development. The fallback is intentional and
 * marked clearly as temporary.</p>
 */
@Component
public class TenantContext {

    /** Temporary demo clinic used only when there is no authenticated user. */
    public static final String DEMO_CLINIC_ID = "clinic-default";

    public AuthenticatedUser currentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser user) {
            return user;
        }
        return null;
    }

    public AuthenticatedUser requireCurrentUser() {
        AuthenticatedUser user = currentUserOrNull();
        if (user == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return user;
    }

    /**
     * Resolves the clinic id for the current request. Uses the authenticated
     * user's clinic when present; otherwise falls back to the demo clinic
     * (local development only).
     */
    public String requireClinicId() {
        AuthenticatedUser user = currentUserOrNull();
        return user != null && user.getClinicId() != null ? user.getClinicId() : DEMO_CLINIC_ID;
    }

    public String currentUserId() {
        AuthenticatedUser user = currentUserOrNull();
        return user != null ? user.getUserId() : null;
    }

    public Role currentRole() {
        AuthenticatedUser user = currentUserOrNull();
        return user != null ? user.getRole() : null;
    }

    public boolean hasRole(Role role) {
        return role == currentRole();
    }
}
