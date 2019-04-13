package io.github.tesla.auth.sdk.jwt.exceptions;

public class InvalidClaimException extends JWTVerificationException {
    public InvalidClaimException(String message) {
        super(message);
    }
}
