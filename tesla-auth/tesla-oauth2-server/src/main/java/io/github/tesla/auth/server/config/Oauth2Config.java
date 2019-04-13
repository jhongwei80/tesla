package io.github.tesla.auth.server.config;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Oauth2Config {

    @Bean
    public OAuthIssuer oAuthIssuer() {
        return new OAuthIssuerImpl(new MD5Generator());
    }

}
