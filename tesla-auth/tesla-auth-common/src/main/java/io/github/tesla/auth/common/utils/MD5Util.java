package io.github.tesla.auth.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 工具类
 */
public class MD5Util {

    /**
     * MD5加密 32位小
     *
     * @param string
     *            加密字符串
     * @return 加密结果字符串
     * @see #md5(String, String)
     */
    public static String md5(String string) {
        return TextUtils.isEmpty(string) ? "" : md5(string, "");
    }

    /**
     * MD5加密(加盐) 32位小
     *
     * @param string
     *            加密字符串
     * @param slat
     *            加密盐值key
     * @return 加密结果字符串
     */
    public static String md5(String string, String slat) {
        if (TextUtils.isEmpty(string))
            return "";

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest((string + slat).getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * MD5加密(多次)
     *
     * @param string
     *            加密字符串
     * @param times
     *            重复加密次数
     * @return 加密结果字符串
     */
    public static String md5(String string, int times) {
        if (TextUtils.isEmpty(string))
            return "";

        String md5 = string;
        for (int i = 0; i < times; i++)
            md5 = md5(md5);
        return md5;
    }

    /**
     * MD5加密(文件) 可用于文件校验。
     *
     * @param file
     *            加密文件
     * @return md5 数值
     */
    public static String md5(File file) {
        if (!file.isFile()) {
            return "";
        }

        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            CloseUtils.close(in);
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
}