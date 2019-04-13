package io.github.tesla.auth.common.utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

public final class JsonUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JsonUtil() {}

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    /**
     * parse and convert a json string to an object
     * <p/>
     * 
     * <pre>
     * MyType a = fromJson("{\"a\": 1, \"b\": 2}", MyType.class);
     * int[] arr = fromJson("[1, 2]", int[].class);
     * List list = fromJson("[1, 2]", List.class);
     * List list = fromJson("[1, 2]", Object.class);
     * Map map = fromJson("{\"a\": 1, \"b\": 2}", Map.class); // same as toMap(json);
     * Map map = fromJson("{\"a\": 1, \"b\": 2}", Object.class); // same as toMap(json);
     * </pre>
     *
     * @return null if there is any exception parsing json string
     */
    public static <T> T fromJson(String json, Class<T> t) {
        if (json == null) {
            return null;
        }
        try {
            return mapper.readValue(json, t);
        } catch (Exception e) {
            LOGGER.info(
                "Cannot parse json string to Object. Json: <" + json + ">, Object class: <" + t.getName() + ">.", e);
        }
        return null;
    }

    /**
     * Convert a JsonArray string to a list of Class<T>
     * 
     * <pre>
     *   List<Integer> l = toList("[1, 2]", Integer.class);
     *   List<MyType> l = toList("[{"a": 2}, {"a": 3}]", MyType.class);
     *   List<Map> l = toList("[{"a": 2}, {"a": 3}]", Map.class);
     *   List<Map> l = toList("[{"a": 2}, {"a": 3}]", Object.class);
     * </pre>
     *
     * @throws throw
     *             IOException if there is invalid json string
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        // .constructParametrizedType is introduced from jackson 2.5
        if (StringUtils.isEmpty(json) || clazz == null) {
            return Collections.emptyList();
        }
        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, clazz);
        try {
            return mapper.readValue(json, javaType);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    /**
     * convert a map to object
     */
    public static <T> T fromMap(Map<?, ?> map, Class<T> t) {
        if (map == null) {
            return null;
        }
        try {
            return mapper.readValue(toJsonString(map), t);
        } catch (Exception e) {
            LOGGER.info("Cannot parse map to Object. Map: <" + map + ">, Object class: <" + t.getName() + ">.", e);
        }
        return null;
    }

    public static Map<?, ?> toMap(String jsonText) {
        return fromJson(jsonText, Map.class);
    }

    /**
     * convert any object to json string
     * 
     * <pre>
     * toJsonString(map) returns{ "b" : "B", "a" : "A" }
     * toJsonString(list) returns ["b", "a"]
     * toJsonString(array) returns ["b", "a"]
     * toJsonString(obj) returns { "fieldA" : "a", "fieldB" : "b" }
     * </pre>
     *
     * @return json string
     */
    public static String toJsonString(Object obj) {
        try {
            if (obj != null) {
                return mapper.writeValueAsString(obj);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot convert to json " + obj);
        }
        return "{}";
    }

    public static String toJsonStr(Object obj, boolean ignoreError) {
        try {
            if (obj != null) {
                return mapper.writeValueAsString(obj);
            }
        } catch (Exception e) {
            LOGGER.debug("convert to json error for object: {}", obj, e);
            if (!ignoreError) {
                throw new IllegalArgumentException("convert to json error for object", e);
            }
        }
        return null;
    }

    /**
     * 将json转换为对象
     */
    public static <T> T convertJsonObject(String json, Class<T> clazz) throws IOException {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 对象序列化为json
     */
    public static <T> String serializeToJson(T object)
        throws IOException {
        return mapper.writeValueAsString(object);
    }

    public static <T> T parse(String json, TypeReference typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static JsonNode tree(String json) {
        JsonNode root = null;
        try {
            root = mapper.readTree(json);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return root;
    }
}