package io.github.tesla.filter.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author liushiming
 * @version JsonUtils.java, v 0.0.1 2018年4月24日 上午10:45:21 liushiming
 */
public class JsonUtils {

    /**
     * 添加space
     *
     * @param sb
     * @param indent
     * @author lizhgb
     * @Date 2015-10-14 上午10:38:04
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }

    public static String convergeJson(List<Pair<String, String>> jsonStrPairList) {
        if (jsonStrPairList == null || jsonStrPairList.size() <= 0) {
            return StringUtils.EMPTY;
        }
        JSONObject result = new JSONObject();
        for (Pair<String, String> jsonStrPair : jsonStrPairList) {
            JSONObject jsonObject = JSON.parseObject(jsonStrPair.getRight());
            if (StringUtils.isBlank(jsonStrPair.getLeft())) {
                result.putAll(jsonObject);
            } else {
                result.put(jsonStrPair.getLeft(), jsonObject);
            }
        }
        return result.toJSONString();
    }

    /**
     * 格式化
     *
     * @param jsonStr
     * @return
     * @author lizhgb
     * @Date 2015-10-14 下午1:17:35
     * @Modified 2017-04-28 下午8:55:35
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr))
            return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMarks = false;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '"':
                    if (last != '\\') {
                        isInQuotationMarks = !isInQuotationMarks;
                    }
                    sb.append(current);
                    break;
                case '{':
                case '[':
                    sb.append(current);
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent++;
                        addIndentBlank(sb, indent);
                    }
                    break;
                case '}':
                case ']':
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent--;
                        addIndentBlank(sb, indent);
                    }
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\' && !isInQuotationMarks) {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static boolean isJson(String content) {
        try {
            JSON.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static <T> T json2Definition(Object filterParam, Class<T> classOfT) {
        if (filterParam == null) {
            return null;
        }
        String paramsJson = (String)filterParam;
        if (StringUtils.isBlank(paramsJson)) {
            return null;
        }
        return fromJson(paramsJson, classOfT);
    }

    public static <T> String serializeToJson(T object) {
        return JSON.toJSONString(object);
    }

}
