package com.logopeda.signature.service;

import com.logopeda.billing.exception.BusinessValidationException;
import com.logopeda.signature.dto.SignatureContext;
import com.logopeda.signature.dto.SignatureResult;
import com.logopeda.signature.enums.SignatureProviderType;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Built-in electronic signature. It is always enabled and produces a verifiable
 * SHA-256 hash over the signed document context (clinic, patient, document and
 * signer identity plus the signing instant). This is a simple electronic
 * signature, not a qualified/advanced one.
 */
@Service
@ConditionalOnProperty(prefix = "app.signature", name = "provider",
        havingValue = "internal-basic", matchIfMissing = true)
public class BasicInternalSignatureProvider implements SignatureProviderPort {

    @Override
    public SignatureProviderType type() {
        return SignatureProviderType.INTERNAL_BASIC;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public SignatureResult sign(SignatureContext context) {
        String payload = String.join("|",
                nullToEmpty(context.clinicId()),
                nullToEmpty(context.patientId()),
                nullToEmpty(context.documentType()),
                nullToEmpty(context.documentId()),
                nullToEmpty(context.signerName()),
                context.signedAt() == null ? "" : context.signedAt().toString());
        return new SignatureResult(SignatureProviderType.INTERNAL_BASIC,
                com.logopeda.signature.enums.SignatureStatus.SIGNED, hash(payload));
    }

    private String hash(String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessValidationException("Unable to compute signature");
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
