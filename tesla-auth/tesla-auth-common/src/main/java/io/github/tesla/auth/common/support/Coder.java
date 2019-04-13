package io.github.tesla.auth.common.support;

import java.util.Base64;

public class Coder {

    /**
     * 解密
     */
    static byte[] decryptBASE64(String key) throws Exception {
        return Base64.getDecoder().decode(key);
    }

    /**
     * 加密
     */
    static String encryptBASE64(byte[] key) throws Exception {
        return Base64.getEncoder().encodeToString(key);
    }
}