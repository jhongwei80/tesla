package io.github.tesla.filter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;

public class MD5Util {
    private static Logger log = LoggerFactory.getLogger(MD5Util.class);

    protected static MessageDigest digest(String type) {
        try {
            return MessageDigest.getInstance(type);
        } catch (NoSuchAlgorithmException e) {
            log.error("获取md5实例出错, [{}]", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * MD5加密(文件) 可用于文件校验。
     *
     * @param file
     *            加密文件
     * @return md5 数值
     */
    public static String md5(File file) {
        if (file != null && !file.isFile()) {
            return "";
        }

        MessageDigest digest = digest("MD5");
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            log.error("计算md5出错, [{}]", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(in);
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * MD5加密 32位小
     *
     * @param string
     *            加密字符串
     * @return 加密结果字符串
     * @see #md5(String, String)
     */
    public static String md5(String string) {
        return StringUtils.isEmpty(string) ? "" : md5(string, "");
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
        if (StringUtils.isEmpty(string))
            return "";

        String md5 = string;
        for (int i = 0; i < times; i++)
            md5 = md5(md5);
        return md5;
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
        if (StringUtils.isEmpty(string))
            return "";
        if (StringUtils.isEmpty(slat))
            slat = "";

        MessageDigest md5 = digest("MD5");
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
    }
}
