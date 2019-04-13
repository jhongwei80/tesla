package io.github.tesla.filter.support.enums;

import java.util.Set;

import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.endpoint.annotation.CreateTokenType;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.plugins.FilterMetadata;
import io.github.tesla.filter.utils.ClassUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/28 15:51
 * @description:
 */
public class CreateTokenTypeEnum {

    private String tokenType;
    private Class<? extends PluginDefinition> definitionClazz;

    CreateTokenTypeEnum(Class clz) {
        CreateTokenType annotation = AnnotationUtils.findAnnotation(clz, CreateTokenType.class);
        this.tokenType = annotation.tokenType();
        this.definitionClazz = annotation.definitionClazz();
    }

    public static CreateTokenTypeEnum fromType(String tokenType) {
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(FilterMetadata.packageName, CreateTokenType.class);
        for (Class clz : allClasses) {
            if (tokenType.equals(AnnotationUtils.findAnnotation(clz, CreateTokenType.class).tokenType())) {
                return new CreateTokenTypeEnum(clz);
            }
        }
        return null;
    }

    public static String validate(String tokenType, String paramJson, ServiceDTO serviceDTO) {
        CreateTokenTypeEnum tokenTypeEnum = fromType(tokenType);
        if (tokenTypeEnum == null) {
            throw new RuntimeException(FilterMetadata.errorMsg(tokenType));
        }
        if (tokenTypeEnum.definitionClazz == null) {
            return paramJson;
        }
        try {
            return fromType(tokenType).definitionClazz.getDeclaredConstructor().newInstance().validate(paramJson,
                serviceDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
