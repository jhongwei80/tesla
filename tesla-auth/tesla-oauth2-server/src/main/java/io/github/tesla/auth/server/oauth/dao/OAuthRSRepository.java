package io.github.tesla.auth.server.oauth.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.tesla.auth.server.oauth.domian.oauth.AccessToken;
import io.github.tesla.auth.server.oauth.domian.oauth.ClientDetails;

/**
 * 15-6-13
 *
 *
 */
@Repository
public class OAuthRSRepository {

    @Autowired
    private OAuthRSMapper oAuthRSMapper;

    public AccessToken findAccessTokenByTokenId(String tokenId) {
        return oAuthRSMapper.findAccessTokenByTokenId(tokenId);
    }

    public ClientDetails findClientDetailsByClientIdAndResourceIds(String clientId, String resourceIds) {
        ClientDetails clientDetails = new ClientDetails();
        clientDetails.setClientId(clientId);
        clientDetails.resourceIds(resourceIds);
        return oAuthRSMapper.findClientDetailsByClientIdAndResourceIds(clientDetails);
    }
}
