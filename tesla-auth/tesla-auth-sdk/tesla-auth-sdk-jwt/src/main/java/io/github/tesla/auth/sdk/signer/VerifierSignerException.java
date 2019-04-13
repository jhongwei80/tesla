package io.github.tesla.auth.sdk.signer;

public class VerifierSignerException extends RuntimeException {
    public VerifierSignerException() {
        super();
    }

    public VerifierSignerException(String message) {
        super(message);
    }

    public VerifierSignerException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerifierSignerException(Throwable cause) {
        super(cause);
    }

    public VerifierSignerException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}