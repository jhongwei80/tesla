package io.github.tesla.filter.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import io.github.tesla.filter.service.definition.PluginDefinition;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AuthType {

    @AliasFor("authType")
    String value() default "";

    @AliasFor("value")
    String authType() default "";

    Class<? extends PluginDefinition> definitionClazz() default PluginDefinition.class;

}
