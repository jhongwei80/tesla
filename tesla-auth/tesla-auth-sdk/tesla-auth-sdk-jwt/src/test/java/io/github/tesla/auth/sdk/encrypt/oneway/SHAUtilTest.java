package io.github.tesla.auth.sdk.encrypt.oneway;

import io.github.tesla.auth.common.utils.SHAUtil;
import org.junit.Assert;
import org.junit.Test;

public class SHAUtilTest {
    private String shaSource = "gogogoddlll";
    private String sha256Dest = "0fe28bb65ff92786b7dd995bc044f09d4c2a2e59eb957842c3687e49a51dd611";

    @Test
    public void sha() {
        String value = SHAUtil.sha(shaSource, SHAUtil.SHA256);
        Assert.assertEquals(sha256Dest, value);
    }
}