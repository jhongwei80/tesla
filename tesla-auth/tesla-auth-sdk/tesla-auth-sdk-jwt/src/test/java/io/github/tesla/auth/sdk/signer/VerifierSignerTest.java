package io.github.tesla.auth.sdk.signer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tesla.auth.sdk.signer.credentials.BkjkCredentials;
import io.github.tesla.auth.sdk.signer.utils.ISO8601Time;
import java.net.URI;
import java.util.Date;
import org.junit.Test;

public class VerifierSignerTest {
  private static final String ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
  private static final String SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
  private static final String SERVICE = "account";

  @Test
  public void shouldSignRequest() throws Exception {
    String hash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
    HttpRequest request = new HttpRequest("PUT", new URI("https://glacier.us-east-1.amazonaws.com/-/vaults/examplevault"));

    String xBKDate = ISO8601Time.getISO8601Timestamp(new Date());
    System.out.println(xBKDate);
    String signature = Signer.builder()
        .bkjkCredentials(new BkjkCredentials(ACCESS_KEY, SECRET_KEY))
        .header(Constants.HOST, "glacier.us-east-1.amazonaws.com")
        .header(Constants.X_BK_DATE, xBKDate)
        .header(Constants.X_BK_EXPIRES, "120")
        .build(request, SERVICE, hash)
        .getSignature();

    System.out.println(signature);
//    String expectedSignature = "BKJK-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20180608/zh-cn-shanghai/account/bkjk_request, SignedHeaders=host;x-bk-date;x-bk-expires, Signature=d9bf9e1cdb9b30a4e32198b49875fbfc7f06197e1658a5ca557c6f928ad7cab5";
//    assertThat(signature).isEqualTo(expectedSignature);

//    String xBKDate = "20180608T222247Z";
//    String signature = "BKJK-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20180608/zh-cn-shanghai/account/bkjk_request, SignedHeaders=host;x-bk-date;x-bk-expires, Signature=f2e8298753c1a8fb9482a8f0d763263f1c9661fa2e9b8ad8cde3f4d9e8b1f30c";

    VerifierSigner.builder(signature) //
        .bkjkCredentials(new BkjkCredentials(ACCESS_KEY, SECRET_KEY))
        .header(new Header(Constants.HOST, "glacier.us-east-1.amazonaws.com")) //
        .header(new Header(Constants.X_BK_DATE, xBKDate)) //
        .header(new Header(Constants.X_BK_EXPIRES, "120"))
        .build(request, SERVICE, hash);
  }
}