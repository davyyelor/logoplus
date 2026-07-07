package com.logopeda.signature.dto;

import com.logopeda.signature.enums.SignatureProviderType;

/** Reports the active signature provider and whether it can produce signatures. */
public record SignatureStatusResponse(SignatureProviderType provider, boolean enabled) {
}
