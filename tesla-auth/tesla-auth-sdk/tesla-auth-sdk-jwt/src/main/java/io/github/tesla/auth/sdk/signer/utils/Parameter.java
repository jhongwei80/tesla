package io.github.tesla.auth.sdk.signer.utils;

public class Parameter {
    private final String name;
    private final String value;

    public Parameter(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}