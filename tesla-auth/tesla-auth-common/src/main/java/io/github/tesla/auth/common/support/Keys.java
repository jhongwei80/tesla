package io.github.tesla.auth.common.support;

/**
 * Created by Tao.Yang on 18/5/29.
 */
public interface Keys {
    String SIGNATURE_ACCESS_KEY = "eyJhbGciOiJIUzI1NiIsIngtc3MiOjEy";
    String SIGNATURE_SECRET_KEY = "EyMDkzMyIsImlzcyI6IlRhby1ZYW5nIi";

    String APP_ID = "KE";
    String SERVICE_NAME_SYMBOL = "bkjk-service";
    String JSON_CONTENT_TYPE = "application/json";
    long EXPIRES_IN = 600;

    String HEADER_DATA_ENCRYPT = "X-BK-Data-Encrypt";
}