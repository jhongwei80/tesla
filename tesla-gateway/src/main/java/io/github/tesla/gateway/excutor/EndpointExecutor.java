package io.github.tesla.gateway.excutor;

import java.io.Serializable;
import java.util.List;

import io.github.tesla.filter.support.enums.HttpMethodEnum;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.support.servlet.NettyHttpServletResponse;
import io.github.tesla.filter.utils.AntMatchUtil;

/**
 * @author: zhangzhiping
 * @date: 2018/11/23 11:05
 * @description:
 */
public class EndpointExecutor implements Comparable<EndpointExecutor>, Serializable {

    private static final long serialVersionUID = 1L;

    private String endPointMethod;

    private String endPointPath;

    private List<ServiceRequestPluginExecutor> requestFiltersList;

    private List<ServiceResponsePluginExecutor> responseFiltersList;

    @Override
    public int compareTo(EndpointExecutor o) {
        /**
         * 1. 精确匹配（method && path） 2. 半精确匹配(method==all && path) 3. 半精确模糊匹配(method && path模糊匹配) 4. 全模糊匹配(method==all &&
         * path模糊匹配)
         */
        if (o == null) {
            return -1;
        }
        if (this.getEndPointPath().equalsIgnoreCase(o.getEndPointPath())) {
            if (this.getEndPointMethod().equals(HttpMethodEnum.ALL.getCode())) {
                return 1;
            } else {
                return -1;
            }
        }
        // 分数= 50*(*个数)+50*(**个数)-path.length
        int thisPathScore = 50 * AntMatchUtil.findCount(this.getEndPointPath(), "*")
            + 50 * AntMatchUtil.findCount(this.getEndPointPath(), "**") - this.getEndPointPath().length();
        int otherPathScore = 50 * AntMatchUtil.findCount(o.getEndPointPath(), "*")
            + 50 * AntMatchUtil.findCount(o.getEndPointPath(), "**") - o.getEndPointPath().length();
        return thisPathScore - otherPathScore;
    }

    ;

    public String getEndPointMethod() {
        return endPointMethod;
    }

    ;

    public String getEndPointPath() {
        return endPointPath;
    }

    public List<ServiceRequestPluginExecutor> getRequestFiltersList() {
        return requestFiltersList;
    }

    public List<ServiceResponsePluginExecutor> getResponseFiltersList() {
        return responseFiltersList;
    }

    public List<ServiceRequestPluginExecutor> matchAndGetFiltes(NettyHttpServletRequest servletRequest) {

        return requestFiltersList;
    }

    public List<ServiceResponsePluginExecutor> matchAndGetFiltes(NettyHttpServletResponse servletResponse) {

        return responseFiltersList;
    }

    public void setEndPointMethod(String endPointMethod) {
        this.endPointMethod = endPointMethod;
    }

    public void setEndPointPath(String endPointPath) {
        this.endPointPath = endPointPath;
    }

    public void setRequestFiltersList(List<ServiceRequestPluginExecutor> requestFiltersList) {
        this.requestFiltersList = requestFiltersList;
    }

    public void setResponseFiltersList(List<ServiceResponsePluginExecutor> responseFiltersList) {
        this.responseFiltersList = responseFiltersList;
    }
}
