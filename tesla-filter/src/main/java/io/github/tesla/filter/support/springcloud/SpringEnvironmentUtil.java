package io.github.tesla.filter.support.springcloud;

import org.springframework.core.env.Environment;

public class SpringEnvironmentUtil {

    public static String getProperty(Environment env, String property) {
        return getProperty(env, property, "");
    }

    public static String getProperty(Environment env, String property, String defaultValue) {
        return env.containsProperty(property) ? env.getProperty(property) : defaultValue;
    }

    public static <T> T getProperty(Environment env, String property, Class<T> clazz) {
        return env.getProperty(property, clazz);
    }

    public static String getPropertyWithEureka(Environment env, String property) {
        return getProperty(env, "eureka.instance." + property, "");
    }
}
