package io.github.tesla.auth.sdk.signer.utils;

import java.util.List;

import io.github.tesla.auth.sdk.signer.encoding.URLEncoding;

public class Normalize {
    public static final char QUERY_PARAMETER_SEPARATOR = '&';
    public static final char QUERY_PARAMETER_VALUE_SEPARATOR = '=';

    public static String convert(List<Parameter> parameters) {
        /*
         * Sort query parameters. Simply sort lexicographically by character
         * code, which is equivalent to comparing code points (as mandated by
         * AWS)
         */
        parameters.sort((l, r) -> l.getName().compareTo(r.getName()));

        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Parameter parameter : parameters) {
            if (first) {
                first = false;
            } else {
                builder.append(QUERY_PARAMETER_SEPARATOR);
            }
            String name = parameter.getName();
            String value = parameter.getValue();
            if (value == null) {
                // No value => use an empty string as per the spec
                value = "";
            }
            builder.append(URLEncoding.encodeQueryComponent(name)).append(QUERY_PARAMETER_VALUE_SEPARATOR)
                .append(URLEncoding.encodeQueryComponent(value));
        }

        return builder.toString();
    }
}