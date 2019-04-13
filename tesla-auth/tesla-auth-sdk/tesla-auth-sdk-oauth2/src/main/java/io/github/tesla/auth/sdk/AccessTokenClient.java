package io.github.tesla.auth.sdk;

import java.net.URI;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import io.github.tesla.auth.common.support.APIResult;
import io.github.tesla.auth.common.support.DESTools;
import io.github.tesla.auth.common.support.ErrorCode;
import io.github.tesla.auth.common.support.Keys;
import io.github.tesla.auth.common.utils.JsonUtil;
import io.github.tesla.auth.sdk.signer.Constants;
import io.github.tesla.auth.sdk.signer.HttpRequest;
import io.github.tesla.auth.sdk.signer.Signer;
import io.github.tesla.auth.sdk.signer.credentials.BkjkCredentials;
import io.github.tesla.auth.sdk.signer.hash.Sha256;

public class AccessTokenClient {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AccessTokenClient.class);
    private String host;
    private AccessTokenFeign accessTokenFeign;

    private static volatile AccessTokenClient instance;
    private static Object mutex = new Object();

    private AccessTokenClient(final String host) throws Exception {
        this.host = host;
        this.accessTokenFeign =
            Feign.builder().encoder(new JacksonEncoder()).decoder(new JacksonDecoder()).logger(new Slf4jLogger()) //
                .logLevel(Logger.Level.FULL).target(AccessTokenFeign.class, host);
    }

    public static AccessTokenClient getInstance(final String host) throws Exception {
        AccessTokenClient result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) {
                    instance = result = new AccessTokenClient(host);
                }
            }
        }
        return result;
    }

    public String applyToken(String appId, String grantType, String scope) throws Exception {
        Map<String, Object> headers = Maps.newHashMap();
        headers.put(Constants.HOST, host);
        final String iso8601UTC = toISO8601UTC(new Date());
        headers.put(Constants.X_BK_DATE, iso8601UTC);
        headers.put("Content-Type", Keys.JSON_CONTENT_TYPE);

        final String accessUrl = host + "/v1/oauth2/token";
        logger.info("accessUrl: {}", accessUrl);
        HttpRequest httpRequest = new HttpRequest("POST", new URI(accessUrl));
        final String json = "{" //
            + "\"appid\":\"" + appId + "\", " //
            + "\"grantType\":\"" + grantType + "\", " //
            + "\"scope\":\"" + scope + "\"" + "}";
        final String encryptData = DESTools.encrypt(json);
        final String contentBodyJson = "{\"data\":\"" + encryptData + "\"}";
        final String contentHash = Sha256.get(contentBodyJson, Charset.forName("UTF8"));
        String signatureValue = Signer.builder().bkjkCredentials( //
            new BkjkCredentials(Keys.SIGNATURE_ACCESS_KEY, Keys.SIGNATURE_SECRET_KEY)).header(Constants.HOST, //
                host.replace("https://", "") //
                    .replace("http://", "")) // not contain schema
            .header(Constants.X_BK_DATE, iso8601UTC) //
            .build(httpRequest, Keys.SERVICE_NAME_SYMBOL, contentHash) //
            .getSignature();
        logger.info("signature: {}", signatureValue);
        headers.put(Constants.X_BK_SIGNATURE, signatureValue);
        APIResult result = accessTokenFeign.apply(headers, encryptData);
        if (ErrorCode.isOk(result.getResult().getCode())) {
            LinkedHashMap<String, String> resultContent = (LinkedHashMap<String, String>)result.getContent();
            String encryptStr = resultContent.get("data");
            String jsonStr = DESTools.decrypt(encryptStr);
            Map<String, String> value = JsonUtil.fromJson(jsonStr, Map.class);
            return value.get("accessToken");
        } else {
            throw new IllegalAccessException(result.getResult().getMessage());
        }
    }

    public Boolean checkedToken(String token) throws Exception {
        Map<String, Object> headers = Maps.newHashMap();
        headers.put(Constants.HOST, host);
        final String iso8601UTC = toISO8601UTC(new Date());
        headers.put(Constants.X_BK_DATE, iso8601UTC);
        headers.put("Content-Type", Keys.JSON_CONTENT_TYPE);

        final String accessUrl = host + "/v1/oauth2/verifyToken";
        logger.info("accessUrl: {}", accessUrl);
        final String json = "{" //
            + "\"accessToken\":\"" + token + "\"" //
            + "}";
        final String encryptData = DESTools.encrypt(json);
        final String contentBodyJson = "{\"data\":\"" + encryptData + "\"}";
        HttpRequest httpRequest = new HttpRequest("POST", new URI(accessUrl));
        final String contentHash = Sha256.get(contentBodyJson, Charset.forName("UTF8"));
        String signatureValue = Signer.builder().bkjkCredentials( //
            new BkjkCredentials(Keys.SIGNATURE_ACCESS_KEY, Keys.SIGNATURE_SECRET_KEY)).header(Constants.HOST, //
                host.replace("https://", "") //
                    .replace("http://", "")) // not contain schema
            .header(Constants.X_BK_DATE, iso8601UTC) //
            .build(httpRequest, Keys.SERVICE_NAME_SYMBOL, contentHash) //
            .getSignature();
        logger.info("signature: {}", signatureValue);
        headers.put(Constants.X_BK_SIGNATURE, signatureValue);
        APIResult result = accessTokenFeign.checked(headers, encryptData);
        if (ErrorCode.isOk(result.getResult().getCode())) {
            LinkedHashMap<String, String> resultContent = (LinkedHashMap<String, String>)result.getContent();
            String encryptStr = resultContent.get("data");
            String jsonStr = DESTools.decrypt(encryptStr);
            Map<String, Boolean> value = JsonUtil.fromJson(jsonStr, Map.class);
            return value.get("enabled");
        } else {
            throw new IllegalAccessException(result.getResult().getMessage());
        }
    }

    private static String toISO8601UTC(Date date) {
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }
}