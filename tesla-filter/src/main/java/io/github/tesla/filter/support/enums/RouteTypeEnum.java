package io.github.tesla.filter.support.enums;

import java.util.Map;

import com.google.common.collect.Maps;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.endpoint.definition.DubboRpcRoutingDefinition;
import io.github.tesla.filter.endpoint.definition.GRpcRoutingDefinition;
import io.github.tesla.filter.service.definition.DirectRoutingDefinition;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.service.definition.SpringCloudRoutingDefinition;

/**
 * @author liushiming
 * @version RouteTypeEnum.java, v 0.0.1 2018年4月23日 下午12:00:50 liushiming
 */
public enum RouteTypeEnum {
    DirectRoute("direct", "直接路由", DirectRoutingDefinition.class),
    DUBBO("dubbo", "Dubbo", DubboRpcRoutingDefinition.class), GRPC("grpc", "gRPC", GRpcRoutingDefinition.class),
    SpringCloud("springcloud", "SpringCloud", SpringCloudRoutingDefinition.class);

    public static RouteTypeEnum fromType(String code) {
        for (RouteTypeEnum routeType : values()) {
            if (routeType.code.equals(code)) {
                return routeType;
            }
        }
        return null;
    }

    public static Map<String, String> toMap() {
        Map<String, String> routeMap = Maps.newHashMap();
        for (RouteTypeEnum routeType : values()) {
            routeMap.put(routeType.getCode(), routeType.getTypeName());
        }
        return routeMap;
    }

    public static String validate(String routerType, String paramJson, ServiceDTO serviceDTO) {
        try {
            return fromType(routerType).clazz.getDeclaredConstructor().newInstance().validate(paramJson, serviceDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String validate(String routerType, String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        try {
            return fromType(routerType).clazz.getDeclaredConstructor().newInstance().validate(paramJson, serviceDTO,
                endpointDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String code;

    private String typeName;

    private Class<? extends PluginDefinition> clazz;

    RouteTypeEnum(String code, String typeName, Class<? extends PluginDefinition> clazz) {
        this.code = code;
        this.typeName = typeName;
        this.clazz = clazz;
    }

    public String getCode() {
        return code;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

}
