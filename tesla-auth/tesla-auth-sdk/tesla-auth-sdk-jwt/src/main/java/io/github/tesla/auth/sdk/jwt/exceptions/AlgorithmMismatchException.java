package io.github.tesla.auth.sdk.jwt.exceptions;

public class AlgorithmMismatchException extends JWTVerificationException {
    public AlgorithmMismatchException(String message) {
        super(message);
    }
}
