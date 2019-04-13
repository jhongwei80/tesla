package io.github.tesla.auth.server.oauth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.tesla.auth.server.oauth.dao.OAuthRSRepository;
import io.github.tesla.auth.server.oauth.domian.oauth.AccessToken;
import io.github.tesla.auth.server.oauth.domian.oauth.ClientDetails;

/**
 * 2015/7/8
 *
 *
 */
@Service("oAuthRSService")
public class OAuthRSService {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthRSService.class);

    @Autowired
    private OAuthRSRepository oAuthRSRepository;

    public AccessToken loadAccessTokenByTokenId(String tokenId) {
        return oAuthRSRepository.findAccessTokenByTokenId(tokenId);
    }

    public ClientDetails loadClientDetails(String clientId, String resourceIds) {
        LOG.debug("Load ClientDetails by clientId: {}, resourceIds: {}", clientId, resourceIds);
        return oAuthRSRepository.findClientDetailsByClientIdAndResourceIds(clientId, resourceIds);
    }

}
