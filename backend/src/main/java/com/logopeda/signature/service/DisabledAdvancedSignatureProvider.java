package com.logopeda.signature.service;

import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.signature.dto.SignatureContext;
import com.logopeda.signature.dto.SignatureResult;
import com.logopeda.signature.enums.SignatureProviderType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Placeholder for an advanced/qualified external signature provider that is not
 * enabled in this deployment. It reports as disabled and rejects signing so the
 * feature can be wired in later behind the same port without code changes.
 */
@Service
@ConditionalOnProperty(prefix = "app.signature", name = "provider", havingValue = "advanced-disabled")
public class DisabledAdvancedSignatureProvider implements SignatureProviderPort {

    @Override
    public SignatureProviderType type() {
        return SignatureProviderType.ADVANCED_DISABLED;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public SignatureResult sign(SignatureContext context) {
        throw new BusinessValidationException("Advanced signature provider is disabled");
    }
}
