package io.github.tesla.filter.support;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;

import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.netty.util.CharsetUtil;

/**
 * @author: zhangzhiping
 * @date: 2019/3/7 18:48
 * @description: 提供从servlet中抽取value的方法
 */
public enum ServletValueKindEnum {

    HTTP_HEADER("HTTP_HEADER", "请求头") {
        @Override
        public <T> T extractValue(NettyHttpServletRequest servletRequest, String merchantIdPath) {
            return (T)servletRequest.getHeader(merchantIdPath);
        }
    },
    HTTP_PARAM("HTTP_PARAM", "请求参数") {
        @Override
        public <T> T extractValue(NettyHttpServletRequest servletRequest, String merchantIdPath) {
            return (T)servletRequest.getParameter(merchantIdPath);
        }
    },
    HTTP_BODY("HTTP_BODY", "请求体") {
        @Override
        public <T> T extractValue(NettyHttpServletRequest servletRequest, String merchantIdPath) {
            try {
                String requestBody = new String(servletRequest.getRequestBody(), CharsetUtil.UTF_8);
                return JsonPath.read(requestBody, merchantIdPath);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            return (T)StringUtils.EMPTY;
        }
    };

    public static ServletValueKindEnum get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (ServletValueKindEnum kind : ServletValueKindEnum.values()) {
            if (code.equalsIgnoreCase(kind.getCode())) {
                return kind;
            }
        }
        return null;
    }

    private String code;

    private String msg;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletValueKindEnum.class);

    ServletValueKindEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public abstract <T> T extractValue(NettyHttpServletRequest servletRequest, String merchantIdPath);

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
