package io.github.tesla.auth.sdk.encrypt.symmetric;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import io.github.tesla.auth.sdk.encrypt.encode.Base64Util;

/**
 * DES 工具类
 */
public class DESUtil {
    private static String algorithm = "DESede";
    private static String transformation = "DESede/ECB/PKCS5Padding";

    private static byte[] encrypt(byte[] data, byte[] key) {
        try {
            SecureRandom sr = new SecureRandom();
            DESedeKeySpec dks = new DESedeKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
            byte[] encryptedData = cipher.doFinal(data);
            return encryptedData;
        } catch (Exception e) {
            System.err.println("DESede算法，加密数据出错!");
            e.printStackTrace();
        }

        return null;
    }

    private static byte[] decrypt(byte[] data, byte[] key) {
        try {
            SecureRandom sr = new SecureRandom();
            DESedeKeySpec dks = new DESedeKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
            byte[] decryptedData = cipher.doFinal(data);
            return decryptedData;
        } catch (Exception e) {
            System.err.println("DESede算法，解密出错。");
            e.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String data, String secret) throws Exception {
        byte[] bytes = Base64Util.decryptBASE64(data);
        return new String(decrypt(bytes, secret.getBytes()), StandardCharsets.UTF_8);
    }

    public static String encrypt(String data, String secret) throws Exception {
        byte[] bytes = encrypt(data.getBytes(Charset.forName("UTF-8")), secret.getBytes());
        return Base64Util.encryptBASE64(bytes);
    }
}