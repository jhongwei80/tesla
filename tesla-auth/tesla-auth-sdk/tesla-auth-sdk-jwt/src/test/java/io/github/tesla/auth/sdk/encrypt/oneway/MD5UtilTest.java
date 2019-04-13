package io.github.tesla.auth.sdk.encrypt.oneway;

import io.github.tesla.auth.common.utils.MD5Util;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MD5UtilTest {
    String md5Source = "md5 is very complex";
    String md5Dest = "add47421d9c4f89fa505e0d5b623b7b1";
    @Test
    public void md5() {
        String value = MD5Util.md5(md5Source);
        Assert.assertEquals(md5Dest, value);
    }
}