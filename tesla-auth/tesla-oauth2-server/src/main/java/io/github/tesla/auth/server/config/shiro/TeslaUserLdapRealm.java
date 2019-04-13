package io.github.tesla.auth.server.config.shiro;

import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.ldap.AbstractLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.apache.shiro.subject.PrincipalCollection;

import io.github.tesla.auth.server.system.dao.AuthzUserDao;

public class TeslaUserLdapRealm extends AbstractLdapRealm {
    private final AuthzUserDao userDao;

    public TeslaUserLdapRealm(AuthzUserDao userDao) {
        this.userDao = userDao;
    }

    public TeslaUserLdapRealm(AuthzUserDao userDao, Map<String, String> ladpMap) {
        if (StringUtils.isBlank(ladpMap.get("searchBase")) || StringUtils.isBlank(ladpMap.get("url"))) {
            throw new RuntimeException("ladp info is null ");
        }
        this.userDao = userDao;
        this.principalSuffix = ladpMap.get("principalSuffix");
        this.searchBase = ladpMap.get("searchBase");
        this.url = ladpMap.get("url");
        super.onInit();
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return true;
    }

    @Override
    protected AuthenticationInfo queryForAuthenticationInfo(AuthenticationToken token,
        LdapContextFactory ldapContextFactory) throws NamingException {
        UsernamePasswordToken upToken = (UsernamePasswordToken)token;
        LdapContext ctx = null;
        try {
            ctx = ldapContextFactory.getLdapContext(upToken.getUsername(), String.valueOf(upToken.getPassword()));
        } catch (Throwable e) {
            return null;
        } finally {
            LdapUtils.closeContext(ctx);
        }
        String username = upToken.getUsername();
        char[] password = upToken.getPassword();
        AuthenticationInfo info = new SimpleAuthenticationInfo(2L, password, username);
        clearCache(info.getPrincipals());
        return info;
    }

    @Override
    protected AuthorizationInfo queryForAuthorizationInfo(PrincipalCollection principals,
        LdapContextFactory ldapContextFactory) throws NamingException {
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
