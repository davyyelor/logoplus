package com.logopeda.shared.enums;

/**
 * Application roles for V1. A user has exactly one role.
 *
 * <ul>
 *   <li>{@code CLINIC_ADMIN} – manages everything inside their clinic.</li>
 *   <li>{@code THERAPIST} – operates on patients of their clinic.</li>
 *   <li>{@code RECEPTION} – manages agenda and administrative data.</li>
 *   <li>{@code FAMILY} – portal user, only sees linked patients.</li>
 * </ul>
 */
public enum Role {
    CLINIC_ADMIN,
    THERAPIST,
    RECEPTION,
    FAMILY
}
