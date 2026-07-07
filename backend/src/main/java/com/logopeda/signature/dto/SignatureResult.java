package com.logopeda.signature.dto;

import com.logopeda.signature.enums.SignatureProviderType;
import com.logopeda.signature.enums.SignatureStatus;

/** Result returned by a signature provider after attempting to sign. */
public record SignatureResult(
        SignatureProviderType provider,
        SignatureStatus status,
        String signatureHash) {
}
