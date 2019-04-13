package io.github.tesla.auth.server.oauth.dao;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.auth.server.oauth.domian.oauth.AccessToken;
import io.github.tesla.auth.server.oauth.domian.oauth.ClientDetails;

/**
 * 15-6-13
 *
 *
 */
@Mapper
public interface OAuthRSMapper {

    AccessToken findAccessTokenByTokenId(String tokenId);

    ClientDetails findClientDetailsByClientIdAndResourceIds(ClientDetails clientDetails);
}
