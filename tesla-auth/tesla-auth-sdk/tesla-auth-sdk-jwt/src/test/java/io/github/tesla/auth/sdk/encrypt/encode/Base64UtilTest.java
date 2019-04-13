package io.github.tesla.auth.sdk.encrypt.encode;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;

public class Base64UtilTest {
    private String helloWorld = "i can fly-in-myself";
    private String base64HelloWorld = "aSBjYW4gZmx5LWluLW15c2VsZg==";

    @Test
    public void decryptBASE64() throws Exception {
        String source = new String(Base64Util.decryptBASE64(base64HelloWorld), Charset.forName("UTF-8"));
        Assert.assertEquals(helloWorld, source);
    }

    @Test
    public void encryptBASE64() throws Exception {
        String base64 = Base64Util.encryptBASE64(helloWorld.getBytes(Charset.forName("UTF-8")));
        Assert.assertEquals(base64HelloWorld, base64);
    }

    @Test
    public void encryptFileBASE64() {
    }

    @Test
    public void decryptFileBASE64() {
    }
}