/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.tesla.ops.config.shiro;

import java.util.List;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import io.github.tesla.ops.system.dao.AuthzUserDao;
import io.github.tesla.ops.system.domain.ShiroUsersDO;
import io.github.tesla.ops.utils.MD5Utils;

/**
 * @author liushiming
 * @version TeslaUserRealm.java, v 0.0.1 2018年1月31日 下午4:14:29 liushiming
 */
public class TeslaUserDbRealm extends AuthorizingRealm {

    private final AuthzUserDao userDao;

    public TeslaUserDbRealm(AuthzUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        UsernamePasswordToken upToken = (UsernamePasswordToken)token;
        String username = upToken.getUsername();
        if (username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }
        ShiroUsersDO user = userDao.findByUserNamed(username);
        if (user == null) {
            return null;
        }
        Long userId = user.userId();
        String password = user.password();
        int status = user.status();
        if (password == null) {
            throw new UnknownAccountException("No account found for " + username);
        }
        String md5Password = MD5Utils.encrypt(username, new String((char[])token.getCredentials()));
        if (!password.equals(md5Password)) {
            throw new IncorrectCredentialsException("Password is not right for " + username);
        }
        if (status == 0) {
            throw new LockedAccountException("account is locked for user " + username);
        }
        if (userId != 1) {
            userId = 2L;
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userId, token.getCredentials(), username);
        info.setCredentialsSalt(ByteSource.Util.bytes(username));
        clearCache(info.getPrincipals());
        return info;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }
        Long userId = (Long)principals.getPrimaryPrincipal();
        List<String> permissions = userDao.findPermissonByUserId(userId);
        List<String> roles = userDao.findRoleByUserId(userId);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roles);
        info.addStringPermissions(permissions);
        return info;
    }

}
