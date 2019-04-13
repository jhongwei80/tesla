package io.github.tesla.auth.sdk.encrypt.symmetric;

import org.junit.Assert;
import org.junit.Test;

public class AESUtilTest {
    String aesSource = "The gate is false!";
    String aesKey = "123WDSseDFS2WEWS";
    String aesDest = "5ebgYnDDQ/PMoERV2KPM+nv/PMvbmnSZsBWDTN/aLZg=";

    @Test
    public void encrypt() {
        String value = AESUtil.encrypt(aesSource, aesKey);
        Assert.assertEquals(aesDest, value);
    }

    @Test
    public void decrypt() {
        String value = AESUtil.decrypt(aesDest, aesKey);
        Assert.assertEquals(aesSource, value);
    }
}