package io.github.tesla.auth.common.support;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DESTools {

    private static String SECRET_KEY = "cf410f84904a44cc8a7f48fc4134e8f9";

    public static String decrypt(String data) throws Exception {
        byte[] bytes = Coder.decryptBASE64(data);
        return new String(DES.decrypt(bytes, SECRET_KEY.getBytes( //
                StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    public static String encrypt(String data) throws Exception {
        final byte[] bytes = DES.encrypt(data.getBytes( //
            Charset.forName("UTF-8")), SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Coder.encryptBASE64(bytes);
    }
}