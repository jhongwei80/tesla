package io.github.tesla.filter.support.enums;

import java.util.Set;

import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.annotation.AuthType;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.plugins.FilterMetadata;
import io.github.tesla.filter.utils.ClassUtils;

public class AuthTypeEnum {
    private String authType;
    private Class<? extends PluginDefinition> clazz;

    AuthTypeEnum(Class clz) {
        AuthType annotation = AnnotationUtils.findAnnotation(clz, AuthType.class);
        this.authType = annotation.authType();
        this.clazz = annotation.definitionClazz();
    }

    public static AuthTypeEnum fromType(String authType) {
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(FilterMetadata.packageName, AuthType.class);
        for (Class clz : allClasses) {
            if (authType.equals(AnnotationUtils.findAnnotation(clz, AuthType.class).authType())) {
                return new AuthTypeEnum(clz);
            }
        }
        return null;
    }

    public static String validate(String authType, String paramJson, ServiceDTO serviceDTO) {
        AuthTypeEnum authTypeEnum = fromType(authType);
        if (authTypeEnum == null) {
            throw new RuntimeException(FilterMetadata.errorMsg(authType));
        }
        if (authTypeEnum.clazz == null) {
            return paramJson;
        }
        try {
            return authTypeEnum.clazz.getDeclaredConstructor().newInstance().validate(paramJson, serviceDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public Class<? extends PluginDefinition> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends PluginDefinition> clazz) {
        this.clazz = clazz;
    }

}
