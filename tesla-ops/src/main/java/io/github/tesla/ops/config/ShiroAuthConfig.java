package io.github.tesla.ops.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.collect.Lists;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import io.github.tesla.ops.common.DataSourceFilter;
import io.github.tesla.ops.config.shiro.LdapProp;
import io.github.tesla.ops.config.shiro.TeslaSessionListener;
import io.github.tesla.ops.config.shiro.TeslaUserDbRealm;
import io.github.tesla.ops.config.shiro.TeslaUserLdapRealm;
import io.github.tesla.ops.system.dao.AuthzUserDao;

@Configuration
public class ShiroAuthConfig {

    @Bean
    public AuthorizationAttributeSourceAdvisor
        authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
            new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public TeslaUserDbRealm dbUserRealm(EhCacheManager ehcacheManager, AuthzUserDao userDao) {
        TeslaUserDbRealm userRealm = new TeslaUserDbRealm(userDao);
        userRealm.setCacheManager(ehcacheManager);
        return userRealm;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    public List<TeslaUserLdapRealm> getLftLdap(AuthzUserDao userDao, LdapProp ldapProp) {
        List<TeslaUserLdapRealm> ldapRealms = Lists.newArrayList();
        if (ldapProp == null || CollectionUtils.isEmpty(ldapProp.getLdaps())) {
            return ldapRealms;
        }
        ldapProp.getLdaps().forEach(ldapMap -> {
            String user = ldapMap.get("user");
            String password = ldapMap.get("password");
            String baseDN = ldapMap.get("baseDN");
            String url = ldapMap.get("url");
            TeslaUserLdapRealm realm = new TeslaUserLdapRealm(userDao, baseDN, url, user, password);
            ldapRealms.add(realm);
        });
        return ldapRealms;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource, true);
    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public SecurityManager securityManager(TeslaUserDbRealm userRealm, AuthzUserDao userDao,
        EhCacheManager cacheManager, LdapProp ldapProp) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        List<Realm> realms = Lists.newArrayList();
        realms.add(userRealm);
        realms.addAll(getLftLdap(userDao, ldapProp));
        manager.setRealms(realms);
        manager.setCacheManager(cacheManager);
        manager.setSessionManager(sessionManager());
        return manager;
    }

    @Bean
    public SessionDAO sessionDAO() {
        MemorySessionDAO sessionDAO = new MemorySessionDAO();
        return sessionDAO;
    }

    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        Collection<SessionListener> listeners = new ArrayList<SessionListener>();
        listeners.add(new TeslaSessionListener());
        sessionManager.setSessionListeners(listeners);
        sessionManager.setSessionDAO(sessionDAO());
        return sessionManager;
    }

    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    @Bean
    public org.apache.shiro.cache.ehcache.EhCacheManager
        shiroEhcacheManager(org.springframework.cache.ehcache.EhCacheCacheManager cacheManager) {
        org.apache.shiro.cache.ehcache.EhCacheManager ehcacheManager =
            new org.apache.shiro.cache.ehcache.EhCacheManager();
        ehcacheManager.setCacheManager(cacheManager.getCacheManager());
        return ehcacheManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        shiroFilterFactoryBean.getFilters().put("dataSourceFilter", new DataSourceFilter());
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/fonts/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
        filterChainDefinitionMap.put("/docs/**", "anon");
        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/upload/**", "anon");
        filterChainDefinitionMap.put("/files/**", "anon");
        filterChainDefinitionMap.put("/oauth/**", "anon");
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/**", "dataSourceFilter,authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

}
