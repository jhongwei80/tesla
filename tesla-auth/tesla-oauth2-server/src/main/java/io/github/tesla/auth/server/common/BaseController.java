package io.github.tesla.auth.server.common;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController {

    protected Logger logger = LoggerFactory.getLogger("controller");

    public static Long getUserId() {
        return (Long)SecurityUtils.getSubject().getPrincipal();
    }

    public static String getUsername() {
        return SecurityUtils.getSubject().getPrincipals().getRealmNames().iterator().next();
    }

    protected CommonResponse getCommonResponse(Exception e) {
        logger.error(e.getMessage(), e);
        String errMsg = e.getMessage();
        if (errMsg.contains(":")) {
            errMsg = errMsg.substring(errMsg.lastIndexOf(":") + 1);
        }
        return CommonResponse.error(errMsg);
    }
}
