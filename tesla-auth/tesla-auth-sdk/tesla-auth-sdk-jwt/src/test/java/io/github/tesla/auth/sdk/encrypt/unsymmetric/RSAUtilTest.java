package io.github.tesla.auth.sdk.encrypt.unsymmetric;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class RSAUtilTest {

    String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJOKYDrM3kAWYuynFvtGjFIQ4ol6F2ODxATZQ36UJTpn\n" +
            "uQr78oigEN3gRzCxSiBZCytgbzL8ZwT72c4limtfFCsCAwEAAQ==";

    String privateKey = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAk4pgOszeQBZi7KcW+0aMUhDiiXoX\n" +
            "Y4PEBNlDfpQlOme5CvvyiKAQ3eBHMLFKIFkLK2BvMvxnBPvZziWKa18UKwIDAQABAkAmDPwAbjGr\n" +
            "iZp9uiIdL/ake6JRHmHF0ofNUFMt4/st8omyzNi6lgm/PjTofirCbLkj2Zdy1NHlJLiSgRdV7dTx\n" +
            "AiEA0HRlVbUCorEqv02qrWE4B0SNiGFoltKTdGfuN9nX32cCIQC1MTjdeprKNqVKt1P9dfsgTp/W\n" +
            "6KfyVjv2LYOZ1KsenQIgaWIuyMGV74H5xnURUE3R8XqqwsPcCEO04CiaugmbpQkCIQCw06ahTsO9\n" +
            "UHMjZaKMGxXHQ7Pt8gPlFo9SAr0J5WhSaQIhAJ0lQxOLyABbNFv7fR36he2b+kXYMZtSk8Esnp/+\n" +
            "So4I";

    String source = "I hsdd to ge" +
            "dfd\nddssa";

    String dest = "Wz39hSruiwjsqeRFuZmqz0bJUHQwWCoMjsrKsqgEuc/hYKncS0Vyr/kQX+IxayJqT6FCuFsZ/KOt\n" +
            "RCZvJEjAjQ==";
    @Test
    public void initRSAKey() throws Exception {
        Map<String, String> map = RSAUtil.initRSAKey();
        System.out.println("publicKey: " + map.get(RSAUtil.PUBLIC_KEY));
        System.out.println("privateKey: " + map.get(RSAUtil.PRIVATE_KEY));
    }

    @Test
    public void encryptionByPublicKey() throws Exception {
        String value = RSAUtil.encryptionByPublicKey(publicKey, source);
        System.out.println(value);
    }

    @Test
    public void decryptionByPrivateKey() throws Exception {
        String value = RSAUtil.decryptionByPrivateKey(privateKey, dest);
        System.out.println(value);
        Assert.assertEquals(source, value);
    }
}