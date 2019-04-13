/*
  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
  the License. You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
  specific language governing permissions and limitations under the License.

  Copyright 2016 the original author or authors.
 */
package io.github.tesla.auth.sdk.signer;

import org.junit.Test;
import io.github.tesla.auth.sdk.signer.credentials.BkjkCredentials;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Richard Lucas
 */
public class SignerTest {

    private static final String ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
    private static final String SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
    private static final String SERVICE = "account";

    @Test
    public void shouldSignRequest() throws Exception {
        // the values used in this test are from the example http://docs.aws.amazon.com/amazonglacier/latest/dev/amazon-glacier-signing-requests.html
        String hash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        HttpRequest request = new HttpRequest("PUT", new URI("https://glacier.us-east-1.amazonaws.com/-/vaults/examplevault"));

        String signature = Signer.builder()
                .bkjkCredentials(new BkjkCredentials(ACCESS_KEY, SECRET_KEY))
                .header("Host", "glacier.us-east-1.amazonaws.com")
                .header("x-bk-date", "20120525T002453Z")
                .header("x-bk-glacier-version", "2012-06-01")
                .build(request, SERVICE, hash)
                .getSignature();

        System.out.println(signature);
        String expectedSignature = "BKJK-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20120525/zh-cn-shanghai/account/bkjk_request, SignedHeaders=host;x-bk-date;x-bk-glacier-version, Signature=9d76f146c3c012c4e6feea1e7ee0c68ff836f214818db2086b9793a0943cee36";
        assertThat(signature).isEqualTo(expectedSignature);
    }

    @Test
    public void shouldSignRequestWithQueryParam() throws Exception {
        String hash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        HttpRequest request = new HttpRequest("GET", new URI("https://examplebucket.s3.amazonaws.com?max-keys=2&prefix=J"));

        String signature = Signer.builder()
                .bkjkCredentials(new BkjkCredentials(ACCESS_KEY, SECRET_KEY))
                .header("Host", "examplebucket.s3.amazonaws.com")
                .header("x-bk-date", "20130524T000000Z")
                .header("x-bk-content-sha256", hash)
                .build(request, SERVICE, hash)
                .getSignature();

        System.out.println(signature);
        String expectedSignature = "BKJK-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20130524/zh-cn-shanghai/account/bkjk_request, SignedHeaders=host;x-bk-content-sha256;x-bk-date, Signature=27a791c13c474baf257b6072b3dc60d6de35a6897ab8653fe5bb1c99f0eb6652";
        assertThat(signature).isEqualTo(expectedSignature);
    }

    @Test
    public void shouldSignStreamingRequest() throws Exception {
        // see http://docs.aws.amazon.com/amazonglacier/latest/dev/amazon-glacier-signing-requests.html
        String contentHash = "79da47e784b181ae04e5b5119fcc953d944acad5e0583fa0899d554a79eb77eb";
        String treeHash = "05c734c3f16b23358bb49c959d1420edac9f28ee844bf9b0580754c0f540acd8";
        URI uri = new URI("https://glacier.us-east-1.amazonaws.com/-/vaults/dev2/multipart-uploads/j3eqysOZoNF3UiEoN3k_b6bdRGGdzgEfsLoUyZhMIwKRMuDLEYRw2nlCh8QXQ_dzqQMxrgFtmZjatxbFIZ9HpnIUi93B");

        HttpRequest request = new HttpRequest("PUT", uri);

        String signature = Signer.builder()
                .bkjkCredentials(new BkjkCredentials(ACCESS_KEY, SECRET_KEY))
                .header("Accept", "application/json")
                .header("Content-Length", "1049350")
                .header("Content-Range", "bytes 0-1049349/*")
                .header("Content-Type", "binary/octet-stream")
                .header("Host", "glacier.us-east-1.amazonaws.com")
                .header("user-agent", "aws-sdk-java/1.9.26 Mac_OS_X/10.10.3 Java_HotSpot(TM)_64-Bit_Server_VM/25.0-b70/1.8.0")
                .header("x-bk-content-sha256", contentHash)
                .header("X-bk-Date", "20150424T222200Z")
                .header("x-bk-glacier-version", "2012-06-01")
                .header("x-bk-sha256-tree-hash", treeHash)
                .header("X-BK-Target", "Glacier.UploadMultipartPart")
                .build(request, SERVICE, contentHash)
                .getSignature();

        System.out.println(signature);
        String expectedSignature = "BKJK-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20150424/zh-cn-shanghai/account/bkjk_request, SignedHeaders=accept;content-length;content-range;content-type;host;user-agent;x-bk-content-sha256;x-bk-date;x-bk-glacier-version;x-bk-sha256-tree-hash;x-bk-target, Signature=670e19153f7c24bd08bc6859df8a02f6a09f7587bfb2d00d89b19588c8118b8b";

        assertThat(signature).isEqualTo(expectedSignature);
    }
}