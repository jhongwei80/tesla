package io.github.tesla.auth.sdk.jwt.algorithms;

import org.apache.commons.codec.binary.Base64;

import io.github.tesla.auth.sdk.jwt.exceptions.SignatureGenerationException;
import io.github.tesla.auth.sdk.jwt.exceptions.SignatureVerificationException;
import io.github.tesla.auth.sdk.jwt.interfaces.DecodedJWT;

class NoneAlgorithm extends Algorithm {

    NoneAlgorithm() {
        super("none", "none");
    }

    @Override
    public void verify(DecodedJWT jwt) throws SignatureVerificationException {
        byte[] signatureBytes = Base64.decodeBase64(jwt.getSignature());
        if (signatureBytes.length > 0) {
            throw new SignatureVerificationException(this);
        }
    }

    @Override
    public byte[] sign(byte[] contentBytes) throws SignatureGenerationException {
        return new byte[0];
    }
}
