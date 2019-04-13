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
package io.github.tesla.auth.sdk.signer.credentials;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.tesla.auth.sdk.signer.SigningException;
import io.github.tesla.auth.sdk.signer.functional.Streams;

/**
 * @author Richard Lucas
 */
public class BkjkCredentialsProviderChain {

    static final String ACCESS_KEY_ENV_VAR = "AWS_ACCESS_KEY";
    static final String SECRET_KEY_ENV_VAR = "AWS_SECRET_KEY";
    static final String ACCESS_KEY_SYSTEM_PROPERTY = "aws.accessKeyId";
    static final String SECRET_KEY_SYSTEM_PROPERTY = "aws.secretKey";

    EnvironmentVarResolver environmentVarResolver;

    private final List<BkjkCredentialsProvider> providers;

    public BkjkCredentialsProviderChain() {
        this(new EnvironmentVarResolver());
    }

    public BkjkCredentialsProviderChain(EnvironmentVarResolver environmentVarResolver) {
        this.environmentVarResolver = environmentVarResolver;
        this.providers = new ArrayList<>();
        this.providers.add(systemPropertiesProvider());
        this.providers.add(environmentProvider());
    }

    public BkjkCredentials getCredentials() {
        return providers.stream().flatMap(p -> Streams.streamopt(p.getCredentials())).findFirst()
            .orElseThrow(() -> new SigningException("no AWS credentials were provided"));
    }

    BkjkCredentialsProvider environmentProvider() {
        return () -> getBkjkCredentials(environmentVarResolver.getenv(ACCESS_KEY_ENV_VAR),
            environmentVarResolver.getenv(SECRET_KEY_ENV_VAR));
    }

    BkjkCredentialsProvider systemPropertiesProvider() {
        return () -> getBkjkCredentials(System.getProperty(ACCESS_KEY_SYSTEM_PROPERTY),
            System.getProperty(SECRET_KEY_SYSTEM_PROPERTY));
    }

    private Optional<BkjkCredentials> getBkjkCredentials(String accessKey, String secretKey) {

        Optional<String> optionalAccessKey = Optional.ofNullable(accessKey);
        Optional<String> optionalSecretKey = Optional.ofNullable(secretKey);
        if (optionalAccessKey.isPresent() && optionalSecretKey.isPresent()) {
            return Optional.of(new BkjkCredentials(optionalAccessKey.get(), optionalSecretKey.get()));
        } else {
            return Optional.empty();
        }
    }

    static class EnvironmentVarResolver {
        String getenv(String name) {
            return System.getenv(name);
        }
    }

}
