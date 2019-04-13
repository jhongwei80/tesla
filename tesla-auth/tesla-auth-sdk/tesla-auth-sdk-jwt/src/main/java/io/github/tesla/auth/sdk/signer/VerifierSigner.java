package io.github.tesla.auth.sdk.signer;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.tesla.auth.sdk.signer.credentials.BkjkCredentials;
import io.github.tesla.auth.sdk.signer.credentials.BkjkCredentialsProviderChain;
import io.github.tesla.auth.sdk.signer.utils.ISO8601Time;

public class VerifierSigner extends BuilderBoss {
    private static String regex = Constants.BKJK_SIGNING_ALGORITHM //
        + " Credential=(\\S*?), SignedHeaders=(\\S*?), Signature=([A-Za-z0-9]+$)";
    private final CanonicalRequest request;
    private final BkjkCredentials bkjkCredentials;
    private final String date;
    private final CredentialScope scope;
    private final String authorizationSignature;

    private VerifierSigner(CanonicalRequest request, BkjkCredentials bkjkCredentials, String date,
        CredentialScope scope, String authorizationSignature) {
        this.request = request;
        this.bkjkCredentials = bkjkCredentials;
        this.date = date;
        this.scope = scope;
        this.authorizationSignature = authorizationSignature;
    }

    public void verify() {
        String calculateSignature = getSignatureBoss(bkjkCredentials, scope, request, date);
        if (!calculateSignature.equals(authorizationSignature)) {
            throw new VerifierSignerException("the calculate signature is not equals expected signature.");
        }
    }

    public static Builder builder(String authorizationSignature) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(authorizationSignature);
        if (!matcher.find()) {
            throw new VerifierSignerException("authorization value is illegal.");
        }
        if (matcher.groupCount() != 3) {
            throw new VerifierSignerException("authorization value is illegal.");
        }

        List<String> signedHeaders = Stream.of(matcher.group(2).trim().split(";")).collect(Collectors.toList());
        return new Builder(signedHeaders, authorizationSignature);
    }

    public static class Builder {
        private List<String> signedHeaders = new ArrayList<>();
        private String authorizationSignature;
        private BkjkCredentials bkjkCredentials;
        private String region = Constants.DEFAULT_REGION;
        private List<Header> headersList = new ArrayList<>();

        Builder(List<String> signedHeaders, String authorizationSignature) {
            this.signedHeaders = signedHeaders;
            this.authorizationSignature = authorizationSignature;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder bkjkCredentials(BkjkCredentials bkjkCredentials) {
            this.bkjkCredentials = bkjkCredentials;
            return this;
        }

        public Builder header(Header header) {
            headersList.add(header);
            return this;
        }

        public Builder headers(Header... headers) {
            Arrays.stream(headers).forEach(headersList::add);
            return this;
        }

        public VerifierSigner build(HttpRequest request, String service, String contentSha256) {
            CanonicalHeaders canonicalHeaders = getCanonicalHeaders();
            String xBKDate = canonicalHeaders.getFirstValue(Constants.X_BK_DATE)
                .orElseThrow(() -> new SigningException("headers missing '" + Constants.X_BK_DATE + "' header"));
            String expiredStr = canonicalHeaders.getFirstValue(Constants.X_BK_EXPIRES).orElse("600");
            expiredStr = expiredStr.trim();
            long expired = Long.parseLong(expiredStr);

            // expired checked
            Date date;
            try {
                date = ISO8601Time.getDateFromIsoDateString(xBKDate);
            } catch (Exception e) {
                e.printStackTrace();
                throw new SigningException("parse the '" + Constants.X_BK_DATE + "' error");
            }
            Date today = new Date();
            today.setTime(today.getTime() - expired * 1000);
            if (date != null && today.after(date)) {
                throw new SigningExpiredException(String.format("The Signature has expired on %s.", date));
            }

            // signature checked
            String dateWithoutTimestamp = formatDateWithoutTimestamp(xBKDate);
            BkjkCredentials bkjkCredentials = getBkjkCredentials();
            CanonicalRequest canonicalRequest = new CanonicalRequest(service, request, canonicalHeaders, contentSha256);
            CredentialScope scope = new CredentialScope(dateWithoutTimestamp, service, region);
            return new VerifierSigner(canonicalRequest, bkjkCredentials, xBKDate, scope, authorizationSignature);
        }

        private BkjkCredentials getBkjkCredentials() {
            return Optional.ofNullable(bkjkCredentials)
                .orElseGet(() -> new BkjkCredentialsProviderChain().getCredentials());
        }

        private CanonicalHeaders getCanonicalHeaders() {
            CanonicalHeaders.Builder builder = CanonicalHeaders.builder();

            // 生成新的headers
            List<Header> newHeaders = new ArrayList<>();
            Map<String, Header> headerMap = headersList.stream().collect(Collectors
                .toMap(k -> k.getName().toLowerCase(), Function.identity(), (key1, key2) -> key2, LinkedHashMap::new));
            for (String headerName : signedHeaders) {
                if (headerMap.containsKey(headerName)) {
                    newHeaders.add(headerMap.get(headerName));
                } else {
                    throw new VerifierSignerException(String.format(//
                        "to be signed header '%s' not to meet it needs.", headerName));
                }
            }

            newHeaders.forEach(h -> builder.add(h.getName(), h.getValue()));
            return builder.build();
        }
    }

}