/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 * Copyright 2016 the original author or authors.
 */
package io.github.tesla.auth.sdk.signer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.github.tesla.auth.sdk.signer.credentials.BkjkCredentials;
import io.github.tesla.auth.sdk.signer.credentials.BkjkCredentialsProviderChain;

/**
 * @author Richard Lucas
 */
public class Signer extends BuilderBoss {
    private final CanonicalRequest request;
    private final BkjkCredentials bkjkCredentials;
    private final String date;
    private final CredentialScope scope;

    private Signer(CanonicalRequest request, BkjkCredentials bkjkCredentials, String date, CredentialScope scope) {
        this.request = request;
        this.bkjkCredentials = bkjkCredentials;
        this.date = date;
        this.scope = scope;
    }

    String getCanonicalRequest() {
        return getCanonicalRequestBoss(request);
    }

    String getStringToSign() {
        return getStringToSignBoss(scope, request, date);
    }

    public String getSignature() {
        return getSignatureBoss(bkjkCredentials, scope, request, date);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BuilderBoss {
        private BkjkCredentials bkjkCredentials;
        private String region = Constants.DEFAULT_REGION;
        private List<Header> headersList = new ArrayList<>();

        public Builder bkjkCredentials(BkjkCredentials bkjkCredentials) {
            this.bkjkCredentials = bkjkCredentials;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder header(String name, String value) {
            headersList.add(new Header(name, value));
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

        public Signer build(HttpRequest request, String service, String contentSha256) {
            CanonicalHeaders canonicalHeaders = getCanonicalHeaders();
            String date = canonicalHeaders.getFirstValue(Constants.X_BK_DATE)
                .orElseThrow(() -> new SigningException("headers missing '" + Constants.X_BK_DATE + "' header"));
            String dateWithoutTimestamp = formatDateWithoutTimestamp(date);
            BkjkCredentials bkjkCredentials = getBkjkCredentials();
            CanonicalRequest canonicalRequest = new CanonicalRequest(service, request, canonicalHeaders, contentSha256);
            CredentialScope scope = new CredentialScope(dateWithoutTimestamp, service, region);
            return new Signer(canonicalRequest, bkjkCredentials, date, scope);
        }

        private BkjkCredentials getBkjkCredentials() {
            return Optional.ofNullable(bkjkCredentials)
                .orElseGet(() -> new BkjkCredentialsProviderChain().getCredentials());
        }

        private CanonicalHeaders getCanonicalHeaders() {
            CanonicalHeaders.Builder builder = CanonicalHeaders.builder();
            headersList.forEach(h -> builder.add(h.getName(), h.getValue()));
            return builder.build();
        }

    }
}
