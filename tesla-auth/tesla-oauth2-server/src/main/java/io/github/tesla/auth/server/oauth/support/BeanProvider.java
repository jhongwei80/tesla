package io.github.tesla.auth.server.oauth.support;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 */
public class BeanProvider implements DisposableBean, ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static ApplicationContext applicationContext;

    private BeanProvider() {}

    /**
     * Get Bean by clazz.
     *
     * @param clazz
     *            Class
     * @param <T>
     *            class type
     * @return Bean instance
     */
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanId) {
        if (applicationContext == null) {
            return null;
        }
        return (T)applicationContext.getBean(beanId);
    }

    public static void clearHolder() {
        applicationContext = null;
    }

    @Override
    public void destroy() throws Exception {
        BeanProvider.clearHolder();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        BeanProvider.applicationContext = applicationContext;
    }

}