package io.github.tesla.filter.support.plugins;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.tesla.filter.support.annnotation.WafRequestPlugin;
import io.github.tesla.filter.utils.ClassUtils;

public class WafRequestPluginMetadata extends RequestPluginMetadata {

    WafRequestPluginMetadata(Class clz) {
        WafRequestPlugin annotation = AnnotationUtils.findAnnotation(clz, WafRequestPlugin.class);
        this.filterType = annotation.filterType();
        this.filterName = annotation.filterName();
        this.filterOrder = annotation.filterOrder();
        this.filterClass = clz;
        this.ignoreClassType = StringUtils.isBlank(annotation.ignoreClassType()) ? null : annotation.ignoreClassType();
        this.definitionClazz = annotation.definitionClazz();
    }

    public static WafRequestPluginMetadata getMetadataByType(String filterType) {
        Set<Class<?>> allClasses = ClassUtils.findAllClasses(packageName, WafRequestPlugin.class);
        for (Class clz : allClasses) {
            if (filterType.equals(AnnotationUtils.findAnnotation(clz, WafRequestPlugin.class).filterType())) {
                return new WafRequestPluginMetadata(clz);
            }
        }
        return null;
    }

    public static String validate(String pluginType, String paramJson) {
        try {
            WafRequestPluginMetadata metadata = getMetadataByType(pluginType);
            if (metadata == null || metadata.definitionClazz == null) {
                return paramJson;
            }
            return metadata.definitionClazz.getDeclaredConstructor().newInstance().validate(paramJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
