package io.github.tesla.auth.server.client.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.github.tesla.auth.server.client.service.OauthClientService;
import io.github.tesla.auth.server.common.BaseController;
import io.github.tesla.auth.server.common.CommonResponse;
import io.github.tesla.auth.server.common.Log;
import io.github.tesla.auth.server.common.Pageable;
import io.github.tesla.auth.server.oauth.domian.oauth.ClientDetails;
import io.github.tesla.auth.server.utils.Query;

@RestController
@RequestMapping("/api/oauth2/client")
public class OauthClientController extends BaseController {

    @Autowired
    private OauthClientService oauthClientService;

    @GetMapping
    public Pageable list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<ClientDetails> clientDetails = oauthClientService.list(query);
        int total = oauthClientService.count(query);
        return new Pageable(clientDetails, total);
    }

    @Log("保存客户端")
    @RequiresPermissions("oauth:client:add")
    @PostMapping
    public CommonResponse save(ClientDetails clientDetails) {
        return oauthClientService.save(clientDetails) ? CommonResponse.ok() : CommonResponse.error();
    }

    @Log("更新客户端")
    @RequiresPermissions("oauth:client:edit")
    @PutMapping
    public CommonResponse update(ClientDetails clientDetails) {
        return oauthClientService.update(clientDetails) ? CommonResponse.ok() : CommonResponse.error();
    }

    @Log("删除客户端")
    @RequiresPermissions("oauth:client:remove")
    @DeleteMapping("/{clientId}")
    public CommonResponse remove(@PathVariable("clientId") String clientId) {
        return oauthClientService.remove(clientId) ? CommonResponse.ok() : CommonResponse.error();
    }

}
