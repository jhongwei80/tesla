package io.github.tesla.auth.server.client.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.tesla.auth.server.client.dao.OauthClientMapper;
import io.github.tesla.auth.server.oauth.domian.oauth.ClientDetails;

@Service
public class OauthClientService {

    @Autowired
    private OauthClientMapper oauthClientMapper;

    public ClientDetails get(String clientId) {
        ClientDetails clientDetails = oauthClientMapper.get(clientId);

        return clientDetails;
    }

    public List<ClientDetails> list(Map<String, Object> map) {
        List<ClientDetails> clientDetails = oauthClientMapper.list(map);

        return clientDetails;
    }

    public int count(Map<String, Object> map) {
        return oauthClientMapper.count(map);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean save(ClientDetails clientDetails) {
        oauthClientMapper.save(clientDetails);
        return true;
    }

    public boolean update(ClientDetails clientDetails) {
        return oauthClientMapper.update(clientDetails) == 1;
    }

    public boolean remove(String clientId) {
        return oauthClientMapper.remove(clientId) == 1;
    }

}
