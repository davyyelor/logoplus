package com.logopeda.signature.enums;

/**
 * Identifies which signature provider produced a signature record.
 *
 * <ul>
 *   <li>{@code INTERNAL_BASIC} — the built-in, always-available simple electronic
 *       signature (a verifiable hash of the signed document).</li>
 *   <li>{@code ADVANCED_DISABLED} — a placeholder for an advanced/qualified
 *       external signature provider that is not enabled in this deployment.</li>
 * </ul>
 */
public enum SignatureProviderType {
    INTERNAL_BASIC,
    ADVANCED_DISABLED
}
