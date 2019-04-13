package io.github.tesla.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@SuppressWarnings("unchecked")
public class SpringContextHolder
    implements DisposableBean, ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LoggerFactory.getLogger(SpringContextHolder.class);
    private static ApplicationContext applicationContext = null;

    public static void clearHolder() {
        applicationContext = null;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> requiredType) {
        try {
            return applicationContext.getBean(requiredType);
        } catch (NoSuchBeanDefinitionException exception) {
            log.debug("not found bean in spring cotext", exception);
            return null;
        }

    }

    public static <T> T getBean(String name) {
        return (T)applicationContext.getBean(name);
    }

    @Override
    public void destroy() throws Exception {
        SpringContextHolder.clearHolder();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }
}
