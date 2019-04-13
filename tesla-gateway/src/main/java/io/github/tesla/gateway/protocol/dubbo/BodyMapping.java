package io.github.tesla.gateway.protocol.dubbo;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

public class BodyMapping {
    private final String body;

    private final Object document;

    private Boolean replace = false;

    public BodyMapping(ByteBuf byteBuf) throws IOException {
        this.body = byteBuf.toString(CharsetUtil.UTF_8);
        this.document = com.jayway.jsonpath.Configuration.builder().options(Option.DEFAULT_PATH_LEAF_TO_NULL).build()
            .jsonProvider().parse(body);
    }

    public BodyMapping(NettyHttpServletRequest request) throws IOException {
        final byte[] bodyContent = request.getRequestBody();
        this.body = new String(bodyContent, CharsetUtil.UTF_8);;
        this.document = com.jayway.jsonpath.Configuration.builder().options(Option.DEFAULT_PATH_LEAF_TO_NULL).build()
            .jsonProvider().parse(body);

    }

    public BodyMapping(String body) {
        this.body = body;
        this.document = com.jayway.jsonpath.Configuration.builder().options(Option.DEFAULT_PATH_LEAF_TO_NULL).build()
            .jsonProvider().parse(body);
    }

    /**
     * <pre>
     * 此函数计算 JSONPath 表达式并以 JSON 字符串形式返回结果。
     * 例如，$input.json('$.pets') 将返回一个表示宠物结构的 JSON 字符串。
     * </pre>
     */
    public String json(String expression) {
        Object json = path(expression);
        if (json instanceof String) {
            return (String)json;
        } else {
            String jsonStr = JSON.toJSONString(json);
            // 对于dubbo而言，这里需要特殊处理一下，需要把"进行处理，其他需要不需要暂时还不确定。。
            if (this.replace) {
                return StringUtils.replaceAll(jsonStr, "\"", "\\\\\"");
            } else {
                return jsonStr;
            }
        }
    }

    /**
     * 返回您的 API 调用的所有请求参数的映射。
     */
    public String params() {
        return body;
    }

    /**
     * <pre>
     * 获取一个 JSONPath 表达式字符串 (x) 并返回结果的对象表示形式。
     * 这样，您便可通过 FreeMarker 模板语言 (VTL) 在本机访问和操作负载的元素。
     * </pre>
     */
    public Object path(String expression) {
        Object obj = JsonPath.parse(document).read(expression);
        return obj;
    }

    public void shouldReplace() {
        this.replace = true;
    }
}
