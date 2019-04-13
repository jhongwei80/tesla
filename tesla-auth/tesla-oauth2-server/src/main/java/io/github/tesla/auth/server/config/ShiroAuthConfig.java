package io.github.tesla.auth.server.config;

import java.util.*;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.collect.Lists;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import io.github.tesla.auth.server.config.shiro.LadpProp;
import io.github.tesla.auth.server.config.shiro.TeslaSessionListener;
import io.github.tesla.auth.server.config.shiro.TeslaUserDbRealm;
import io.github.tesla.auth.server.config.shiro.TeslaUserLdapRealm;
import io.github.tesla.auth.server.oauth.support.BeanProvider;
import io.github.tesla.auth.server.system.dao.AuthzUserDao;

@Configuration
public class ShiroAuthConfig {

    @Bean
    public org.apache.shiro.cache.ehcache.EhCacheManager
        shiroEhcacheManager(org.springframework.cache.ehcache.EhCacheCacheManager cacheManager) {
        org.apache.shiro.cache.ehcache.EhCacheManager ehcacheManager =
            new org.apache.shiro.cache.ehcache.EhCacheManager();
        ehcacheManager.setCacheManager(cacheManager.getCacheManager());
        return ehcacheManager;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource, true);
    }

    @Bean
    public TeslaUserDbRealm dbUserRealm(EhCacheManager ehcacheManager, DataSource dataSource, AuthzUserDao userDao) {
        TeslaUserDbRealm userRealm = new TeslaUserDbRealm(userDao);
        userRealm.setCacheManager(ehcacheManager);
        return userRealm;
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

    public List<TeslaUserLdapRealm> getLadpList(AuthzUserDao userDao, List<Map<String, String>> ladpList) {
        List<TeslaUserLdapRealm> ldapRealms = Lists.newArrayList();
        if (CollectionUtils.isEmpty(ladpList)) {
            return ldapRealms;
        }
        ladpList.forEach(ladpMap -> {
            TeslaUserLdapRealm realm = new TeslaUserLdapRealm(userDao, ladpMap);
            ldapRealms.add(realm);
        });
        return ldapRealms;
    }

    @Bean
    public SecurityManager securityManager(TeslaUserDbRealm userRealm, AuthzUserDao userDao,
        EhCacheManager cacheManager, LadpProp ladpProp, @Value("${shiro.useLocalDbRealm:true}") boolean useLocalDbRealm,
        @Value("${shiro.specialRealmBean:#{null}}") String specialRealmBean) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        List<Realm> realms = Lists.newArrayList();
        realms.addAll(getLadpList(userDao, ladpProp.getLadps()));
        if (useLocalDbRealm) {
            realms.add(userRealm);
        }
        if (StringUtils.isNotBlank(specialRealmBean)) {
            if (BeanProvider.getBean(specialRealmBean) != null) {
                realms.add(BeanProvider.getBean(specialRealmBean));
            }
        }
        manager.setRealms(realms);
        manager.setCacheManager(cacheManager);
        manager.setSessionManager(sessionManager());
        return manager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/fonts/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
        filterChainDefinitionMap.put("/docs/**", "anon");
        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/oauth/**", "anon");
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor
        authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
            new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
