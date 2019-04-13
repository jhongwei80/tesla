package io.github.tesla.filter.support.annnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.tesla.filter.service.definition.PluginDefinition;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface WafResponsePlugin {

    String filterType();

    String filterName();

    int filterOrder();

    String ignoreClassType() default "";

    Class<? extends PluginDefinition> definitionClazz() default PluginDefinition.class;
}
