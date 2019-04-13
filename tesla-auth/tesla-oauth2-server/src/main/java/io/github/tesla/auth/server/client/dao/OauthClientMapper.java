package io.github.tesla.auth.server.client.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.auth.server.oauth.domian.oauth.ClientDetails;

@Mapper
public interface OauthClientMapper {

    ClientDetails get(String clientId);

    List<ClientDetails> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(ClientDetails clientDetails);

    int update(ClientDetails clientDetails);

    int remove(String clientId);
}
