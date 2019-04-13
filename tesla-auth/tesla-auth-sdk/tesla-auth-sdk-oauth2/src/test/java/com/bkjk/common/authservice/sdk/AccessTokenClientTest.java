package com.bkjk.common.authservice.sdk;

import io.github.tesla.auth.sdk.AccessTokenClient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

/**
 * @Author: wangzhiguo
 * @Date: 2018/11/27 14:03
 */
public class AccessTokenClientTest {

    @Ignore
    @Test
    public void testApplyToken() throws Exception {
        AccessTokenClient client = AccessTokenClient.getInstance("http://localhost:8080");
        String token = client.applyToken("KE", "clientcredentials", "bkjkapi_base");
        System.out.println(token);
        Assert.assertFalse(null == token);
    }

    @Ignore
    @Test
    public void testCheckedToken() throws Exception {
        AccessTokenClient client = AccessTokenClient.getInstance("http://localhost:8080");
        Boolean valid = client.checkedToken("6e25e483b18a7cd704bacdf40657800c");
        System.out.println(valid);
        Assert.assertFalse(valid);
    }

    @Ignore
    @Test
    public void testApplyTokenExt() throws Exception {
        AccessTokenClient client = AccessTokenClient.getInstance("http://localhost:8080");
        for (int i = 0; i < 100; i++) {
            Random random = new Random();
            int num = random.nextInt(10);
            num = (num == 0) ? 1 : num;
            String appId = randomString(num);
            String token = client.applyToken(appId, "clientcredentials", "bkjkapi_base");
            System.out.println("index " + i + ", appId: " + appId + ", token: " + token);
            Assert.assertFalse(null == token);
        }
    }

    private static String randomString(int a) {
        char[] cs = new char[a];
        String pool = "";
        //0123456789ABCDEF..Zabcd..z
        for (short i = '0'; i <= '9'; i++) {
            pool = pool + (char) i;
        }
        for (short i = 'A'; i <= 'Z'; i++) {
            pool = pool + (char) i;
        }
        for (short i = 'a'; i <= 'z'; i++) {
            pool = pool + (char) i;
        }
        for (int h = 0; h < cs.length; h++) {
            int index = (int) (Math.random() * pool.length());//产生一个pool范围内的随机数
            cs[h] = pool.charAt(index);//返回指定索引处的字符
        }
        String str1 = new String(cs);
        return str1;
    }
}