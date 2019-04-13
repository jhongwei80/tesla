package io.github.tesla.auth.sdk.signer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.github.tesla.auth.sdk.signer.credentials.BkjkCredentials;
import io.github.tesla.auth.sdk.signer.hash.Base16;
import io.github.tesla.auth.sdk.signer.hash.Sha256;

public class BuilderBoss {
    static String formatDateWithoutTimestamp(String date) {
        return date.substring(0, 8);
    }

    static String getCanonicalRequestBoss(CanonicalRequest request) {
        return request.get();
    }

    static String getStringToSignBoss(CredentialScope scope, CanonicalRequest request, String date) {
        String hashedCanonicalRequest = Sha256.get(getCanonicalRequestBoss(request), Constants.UTF_8);
        return buildStringToSign(date, scope.get(), hashedCanonicalRequest);
    }

    static String getSignatureBoss(BkjkCredentials bkjkCredentials, CredentialScope scope, CanonicalRequest request,
        String date) {
        String signature =
            buildSignature(bkjkCredentials.getSecretKey(), scope, getStringToSignBoss(scope, request, date));
        return buildAuthHeader(bkjkCredentials.getAccessKey(), scope.get(), request.getHeaders().getNames(), signature);
    }

    static String buildStringToSign(String date, String credentialScope, String hashedCanonicalRequest) {
        return Constants.BKJK_SIGNING_ALGORITHM + "\n" + date + "\n" + credentialScope + "\n" + hashedCanonicalRequest;
    }

    static String buildAuthHeader(String accessKey, String credentialScope, String signedHeaders, String signature) {
        return Constants.BKJK_SIGNING_ALGORITHM + " " + "Credential=" + accessKey + "/" + credentialScope + ", "
            + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature;
    }

    static byte[] hmacSha256(byte[] key, String value) {
        try {
            String algorithm = Constants.HMAC_SHA256_ALGORITHM;
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec signingKey = new SecretKeySpec(key, algorithm);
            mac.init(signingKey);
            return mac.doFinal(value.getBytes(Constants.UTF_8));
        } catch (Exception e) {
            throw new SigningException("Error signing request", e);
        }
    }

    static String buildSignature(String secretKey, CredentialScope scope, String stringToSign) {
        byte[] kSecret = (Constants.AUTH_TAG + secretKey).getBytes(Constants.UTF_8);
        byte[] kDate = hmacSha256(kSecret, scope.getDateWithoutTimestamp());
        byte[] kRegion = hmacSha256(kDate, scope.getRegion());
        byte[] kService = hmacSha256(kRegion, scope.getService());
        byte[] kSigning = hmacSha256(kService, Constants.BKJK4_TERMINATOR);
        return Base16.encode(hmacSha256(kSigning, stringToSign)).toLowerCase();
    }
}