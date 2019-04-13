package io.github.tesla.auth.sdk.jwt.exceptions;

public class JWTCreationException extends RuntimeException {
    public JWTCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
