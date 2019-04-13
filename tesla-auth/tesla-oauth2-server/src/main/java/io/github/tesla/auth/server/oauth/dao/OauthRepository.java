package io.github.tesla.auth.server.oauth.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.tesla.auth.server.oauth.domian.oauth.AccessToken;
import io.github.tesla.auth.server.oauth.domian.oauth.ClientDetails;
import io.github.tesla.auth.server.oauth.domian.oauth.OauthCode;

/**
 * ${todo}
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 16:08
 * @version: V1.0.0
 * @since JDK 11
 */
@Repository
public class OauthRepository {
    @Autowired
    private OauthMapper oauthMapper;

    public ClientDetails findClientDetails(String clientId) {
        List<ClientDetails> list = findClientDetailsListByClientId(clientId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public int saveClientDetails(ClientDetails clientDetails) {
        return oauthMapper.saveClientDetails(clientDetails);
    }

    public int saveOauthCode(OauthCode oauthCode) {
        return oauthMapper.saveOauthCode(oauthCode);
    }

    public OauthCode findOauthCode(String code, String clientId) {
        OauthCode oauthCode = new OauthCode().code(code).clientId(clientId);
        return oauthMapper.findOauthCode(oauthCode);
    }

    public OauthCode findOauthCodeByUsernameClientId(String username, String clientId) {
        OauthCode oauthCode = new OauthCode().username(username).clientId(clientId);
        return oauthMapper.findOauthCode(oauthCode);
    }

    public int deleteOauthCode(OauthCode oauthCode) {
        return oauthMapper.deleteOauthCode(oauthCode);
    }

    public AccessToken findAccessToken(String clientId, String username, String authenticationId) {
        AccessToken accessToken =
            new AccessToken().clientId(clientId).username(username).authenticationId(authenticationId);
        return oauthMapper.findAccessToken(accessToken);
    }

    public int deleteAccessToken(AccessToken accessToken) {
        return oauthMapper.deleteAccessToken(accessToken);
    }

    public int saveAccessToken(AccessToken accessToken) {
        return oauthMapper.saveAccessToken(accessToken);
    }

    public AccessToken findAccessTokenByRefreshToken(String refreshToken, String clientId) {
        AccessToken accessToken = new AccessToken().refreshToken(refreshToken).clientId(clientId);
        return oauthMapper.findAccessToken(accessToken);
    }

    public List<ClientDetails> findClientDetailsListByClientId(String clientId) {
        return oauthMapper.findClientDetailsListByClientId(clientId);
    }

}
