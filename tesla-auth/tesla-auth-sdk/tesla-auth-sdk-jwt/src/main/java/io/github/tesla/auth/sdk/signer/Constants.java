package io.github.tesla.auth.sdk.signer;

import java.nio.charset.Charset;

import io.github.tesla.auth.sdk.signer.functional.Throwables;

public class Constants {
    public static final Charset UTF_8 =
        Throwables.returnableInstance(() -> Charset.forName("UTF-8"), SigningException::new);

    public static final String LINE_SEPARATOR = "\n";

    public static final String DEFAULT_REGION = "zh-cn-shanghai";

    public static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    public static final String AUTH_TAG = "BKJK";

    public static final String BKJK_SIGNING_ALGORITHM = AUTH_TAG + "-HMAC-SHA256";

    public static final String AUTHORIZATION = "Authorization";

    public static final String HOST = "Host";

    public static final String BKJK4_TERMINATOR = AUTH_TAG.toLowerCase() + "_request";

    public static final String X_BK_DATE = "X-BK-Date";

    public static final String X_BK_SECURITY_TOKEN = "X-BK-Security-Token";

    public static final String X_BK_CREDENTIAL = "X-BK-Credential";

    public static final String X_BK_EXPIRES = "X-BK-Expires";

    public static final String X_BK_SIGNED_HEADER = "X-BK-SignedHeaders";

    public static final String X_BK_CONTENT_SHA256 = "X-BK-Content-SHA256";

    public static final String X_BK_SIGNATURE = "X-BK-Signature";

    public static final String X_BK_ALGORITHM = "X-BK-Algorithm";
}