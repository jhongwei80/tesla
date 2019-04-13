package io.github.tesla.filter.endpoint.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import io.github.tesla.filter.service.definition.PluginDefinition;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CreateTokenType {

    @AliasFor("tokenType")
    String value() default "";

    @AliasFor("value")
    String tokenType() default "";

    Class<? extends PluginDefinition> definitionClazz() default PluginDefinition.class;

}
