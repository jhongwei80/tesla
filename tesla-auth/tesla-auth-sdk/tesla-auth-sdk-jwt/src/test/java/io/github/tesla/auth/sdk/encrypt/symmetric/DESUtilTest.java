package io.github.tesla.auth.sdk.encrypt.symmetric;

import org.junit.Assert;
import org.junit.Test;

public class DESUtilTest {
    String desSource = "The gate is false!";
    String desKey = "DE123dssDFS2WEWSDE123dssDFS2WEWS";
    String desDest = "MpeqjD5Levm6vj1zaRKFFo8/v8PhH1QP";

    @Test
    public void decrypt() throws Exception {
        String value = DESUtil.decrypt(desDest, desKey);
        Assert.assertEquals(desSource, value);
    }

    @Test
    public void encrypt() throws Exception {
        String value = DESUtil.encrypt(desSource, desKey);
        Assert.assertEquals(desDest, value);
    }
}