package io.github.tesla.auth.sdk.encrypt.symmetric;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.github.tesla.auth.sdk.encrypt.encode.Base64Util;

/**
 * AES 工具类
 */
public class AESUtil {
    private static String charset = "UTF-8";

    // 偏移量
    private static int offset = 16;
    private static String transformation = "AES/CBC/PKCS5Padding";
    private static String algorithm = "AES";

    /**
     * 加密
     *
     * @param content
     *            需要加密的内容
     * @param key
     *            加密密码
     * @return
     */
    public static String encrypt(String content, String key) {
        try {
            SecretKeySpec secretkey = new SecretKeySpec(key.getBytes(), algorithm);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, offset);
            Cipher cipher = Cipher.getInstance(transformation);
            byte[] byteContent = content.getBytes(charset);
            cipher.init(Cipher.ENCRYPT_MODE, secretkey, iv);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return Base64Util.encryptBASE64(result); // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES（256）解密
     *
     * @param content
     *            待解密内容
     * @param key
     *            解密密钥
     * @return 解密之后
     * @throws Exception
     */
    public static String decrypt(String content, String key) {
        try {
            SecretKeySpec secretkey = new SecretKeySpec(key.getBytes(), algorithm);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, offset);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secretkey, iv);// 初始化
            byte[] result = cipher.doFinal(Base64Util.decryptBASE64(content));
            return new String(result); // 解密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}