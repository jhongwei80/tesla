package io.github.tesla.auth.server.oauth.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.auth.server.oauth.domian.oauth.AccessToken;
import io.github.tesla.auth.server.oauth.domian.oauth.ClientDetails;
import io.github.tesla.auth.server.oauth.domian.oauth.OauthCode;

@Mapper
public interface OauthMapper {

    int saveClientDetails(ClientDetails clientDetails);

    int saveOauthCode(OauthCode oauthCode);

    OauthCode findOauthCode(OauthCode oauthCode);

    int deleteOauthCode(OauthCode oauthCode);

    AccessToken findAccessToken(AccessToken accessToken);

    int deleteAccessToken(AccessToken accessToken);

    int saveAccessToken(AccessToken accessToken);

    List<ClientDetails> findClientDetailsListByClientId(String clientId);
}
