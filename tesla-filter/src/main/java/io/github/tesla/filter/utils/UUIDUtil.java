package io.github.tesla.filter.utils;

import java.util.Random;
import java.util.UUID;

public class UUIDUtil {
    public static String[] chars = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
        "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
        "W", "X", "Y", "Z"};

    public static void main(String[] args) {
        System.out.printf(randomSecret());
    }

    public static String randomSecret() {
        Random random = new Random();
        StringBuffer secretBuffer = new StringBuffer();
        for (int i = 0; i < 32; i++) // 生成指定位数的随机秘钥字符串
        {
            secretBuffer.append(chars[random.nextInt(chars.length)]);
        }
        return secretBuffer.toString();

    }

    public static String uuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();

    }

}
