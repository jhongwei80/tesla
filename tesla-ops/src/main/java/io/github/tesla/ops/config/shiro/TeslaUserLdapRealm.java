package io.github.tesla.ops.config.shiro;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.tesla.ops.system.dao.AuthzUserDao;

/**
 * @author: zhangzhiping
 * @date: 2019/1/16 17:02
 * @description:
 */
public class TeslaUserLdapRealm extends AuthorizingRealm {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeslaUserLdapRealm.class);
    private final static Control[] connCtls = null;
    private final AuthzUserDao userDao;
    private String URL;
    private String BASEDN;
    private String USER;
    private String PASSWORD;

    public TeslaUserLdapRealm(AuthzUserDao userDao, String baseDN, String url, String user, String password) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        this.BASEDN = baseDN;
        this.URL = url;
        this.USER = user;
        this.PASSWORD = password;
        this.userDao = userDao;
    }

    @SuppressWarnings("unused")
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        UsernamePasswordToken upToken = (UsernamePasswordToken)token;
        String username = upToken.getUsername();
        if (username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }
        LdapContext ctx = null;
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, URL + BASEDN);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        env.put(Context.SECURITY_PRINCIPAL, USER); // 管理员
        env.put(Context.SECURITY_CREDENTIALS, PASSWORD); // 管理员密码
        try {
            ctx = new InitialLdapContext(env, connCtls);

            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = "(&(objectClass=user)(sAMAccountName=" + username + "))";
            NamingEnumeration<SearchResult> en = ctx.search("", filter, constraints);

            if (en == null || !en.hasMoreElements()) {
                LOGGER.info("LDAP未找到该用户username:" + username);
                return null;
            }
            String userDN = "";
            // maybe more than one element
            while (en != null && en.hasMoreElements()) {
                Object obj = en.nextElement();
                if (obj instanceof SearchResult) {
                    SearchResult si = (SearchResult)obj;
                    userDN += si.getName();
                    userDN += "," + BASEDN;
                } else {
                    LOGGER.error("obj:" + obj);
                }
            }

            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, String.valueOf(upToken.getPassword()));
            ctx.reconnect(connCtls);
            if (ctx != null) {
                // 给默认的权限，可优化
                long userId = 2L;
                SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userId, token.getCredentials(), username);
                info.setCredentialsSalt(ByteSource.Util.bytes(username));
                clearCache(info.getPrincipals());
                return info;
            }
        } catch (Exception e) {
            LOGGER.info("LDAP未找到该用户username:" + username);
            return null;
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (final NamingException e) {
                    return null;
                }
            }
        }
        return null;
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
