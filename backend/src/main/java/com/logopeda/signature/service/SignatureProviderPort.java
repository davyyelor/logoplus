package com.logopeda.signature.service;

import com.logopeda.signature.dto.SignatureContext;
import com.logopeda.signature.dto.SignatureResult;
import com.logopeda.signature.enums.SignatureProviderType;

/**
 * Port abstracting the electronic-signature backend. Implementations are
 * selected via {@code app.signature.provider} so the platform can run with the
 * built-in basic signature (default), or expose a disabled placeholder for an
 * external advanced/qualified signature integration. No implementation performs
 * clinical decisioning; a signature is purely an authenticity record.
 */
public interface SignatureProviderPort {

    SignatureProviderType type();

    boolean isEnabled();

    SignatureResult sign(SignatureContext context);
}
