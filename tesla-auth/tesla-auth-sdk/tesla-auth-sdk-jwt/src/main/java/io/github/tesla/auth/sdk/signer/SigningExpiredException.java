package io.github.tesla.auth.sdk.signer;

public class SigningExpiredException extends RuntimeException {
    public SigningExpiredException() {
        super();
    }

    public SigningExpiredException(String message) {
        super(message);
    }

    public SigningExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public SigningExpiredException(Throwable cause) {
        super(cause);
    }

    public SigningExpiredException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}