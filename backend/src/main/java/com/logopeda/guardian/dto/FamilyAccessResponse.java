package com.logopeda.guardian.dto;

/** Result of enabling portal access for a guardian. */
public record FamilyAccessResponse(
        String guardianId,
        String userId,
        String email) {
}
