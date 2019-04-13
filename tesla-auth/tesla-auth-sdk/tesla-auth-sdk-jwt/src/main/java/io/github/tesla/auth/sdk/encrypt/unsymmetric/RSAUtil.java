package io.github.tesla.auth.sdk.encrypt.unsymmetric;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import io.github.tesla.auth.sdk.encrypt.encode.Base64Util;

/**
 * RSA 工具类
 */
public class RSAUtil {
    private static String ALGORITHM_RSA = "RSA";
    private static final int KEY_SIZE = 512;
    public static final String PUBLIC_KEY = "RSAPublicKey";
    public static final String PRIVATE_KEY = "RSAPrivateKey";

    public static Map<String, String> initRSAKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey)keyPair.getPublic();
        String publicKey = Base64Util.encryptBASE64(rsaPublicKey.getEncoded());

        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)keyPair.getPrivate();
        String privateKey = Base64Util.encryptBASE64(rsaPrivateKey.getEncoded());

        Map<String, String> rsaKey = new HashMap<>();
        rsaKey.put(PUBLIC_KEY, publicKey);
        rsaKey.put(PRIVATE_KEY, privateKey);
        return rsaKey;
    }

    public static String encryptionByPublicKey(String publicKeyString, String source) throws Exception {
        PublicKey publicKey = getPublicKey(publicKeyString);
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        cipher.update(source.getBytes(StandardCharsets.UTF_8));
        String target = Base64Util.encryptBASE64(cipher.doFinal());
        return target;
    }

    public static String decryptionByPrivateKey(String privateKeyString, String target) throws Exception {
        PrivateKey privateKey = getPrivateKey(privateKeyString);
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        cipher.update(Base64Util.decryptBASE64(target));
        String source = new String(cipher.doFinal(), StandardCharsets.UTF_8);
        return source;
    }

    private static PublicKey getPublicKey(String publicKey) throws Exception {
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64Util.decryptBASE64(publicKey));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        return keyFactory.generatePublic(publicKeySpec);
    }

    private static PrivateKey getPrivateKey(String privateKey) throws Exception {
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64Util.decryptBASE64(privateKey));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        return keyFactory.generatePrivate(privateKeySpec);
    }
}