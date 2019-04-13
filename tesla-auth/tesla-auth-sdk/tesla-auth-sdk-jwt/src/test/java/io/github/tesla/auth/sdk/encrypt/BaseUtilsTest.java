package io.github.tesla.auth.sdk.encrypt;

import io.github.tesla.auth.common.utils.BaseUtils;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;

public class BaseUtilsTest {
    private String helloWorld = "how-to-get-it";
    private String hexHelloWorld = "686F772D746F2D6765742D6974";
    @Test
    public void parseByte2HexString() {
        String hex = BaseUtils.parseByte2HexString(helloWorld.getBytes(Charset.forName("UTF-8")));
        Assert.assertEquals(hexHelloWorld, hex);
    }

    @Test
    public void parseHexString2Byte() {
        byte[] bytes = BaseUtils.parseHexString2Byte(hexHelloWorld);
        Assert.assertEquals(helloWorld, new String(bytes, Charset.forName("UTF-8")));
    }
}