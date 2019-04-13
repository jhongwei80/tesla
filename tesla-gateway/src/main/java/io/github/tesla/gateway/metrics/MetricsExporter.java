package io.github.tesla.gateway.metrics;

import com.bkjk.platform.monitor.Monitors;

import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;

public abstract class MetricsExporter {

    static final class MonitorEvent {
        private String method;
        private String uri;
        private Integer statusCode;
        private String param;

        public MonitorEvent(String method, String uri, Integer statusCode, String param) {
            super();
            this.method = method;
            this.uri = uri;
            this.statusCode = statusCode;
            this.param = param;
        }

        public String getMethod() {
            return method;
        }

        public String getParam() {
            return param;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public String getUri() {
            return uri;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

    }

    private static PromethusMetricsExporter PROMETHUS = new PromethusMetricsExporter();

    private static MicrometerMetricsExporter MICROMETER = new MicrometerMetricsExporter();

    public static void forward(String method, String uri, String forwardParam, NettyHttpServletRequest request) {
        Monitors.logEvent("FORWARD STARTED", new MonitorEvent(method, uri, null, forwardParam));
        Object promethusObj = PROMETHUS.forwardStart(method, uri);
        request.setAttribute("promethusForwardObj", promethusObj);
        Object micrmeterObj = MICROMETER.forwardStart(method, uri);
        request.setAttribute("micrmeterForwardeObj", micrmeterObj);
    }

    public static void receive(String method, String uri, String requestParam, NettyHttpServletRequest request) {
        Monitors.logEvent("REQUEST STARTED", new MonitorEvent(method, uri, null, requestParam));
        Object promethusObj = PROMETHUS.requestStart(method, uri);
        request.setAttribute("promethusReceiveObj", promethusObj);
        Object micrmeterObj = MICROMETER.requestStart(method, uri);
        request.setAttribute("micrmeterReceiveObj", micrmeterObj);
    }

    public static void receiveSize(String method, String uri, int size) {
        PROMETHUS.requestSize(method, uri, size);
        MICROMETER.requestSize(method, uri, size);
    }

    public static void returned(String method, String uri, int statusCode, NettyHttpServletRequest request) {
        Monitors.logEvent("REQUEST END", new MonitorEvent(method, uri, statusCode, null));
        PROMETHUS.requestEnd(method, uri, statusCode, request.getAttribute("promethusReceiveObj"),
            request.getAttribute("promethusForwardObj"));
        MICROMETER.requestEnd(method, uri, statusCode, request.getAttribute("micrmeterReceiveObj"),
            request.getAttribute("micrmeterForwardeObj"));
        request.removeAttribute("promethusReceiveObj");
        request.removeAttribute("promethusForwardObj");
        request.removeAttribute("micrmeterReceiveObj");
        request.removeAttribute("micrmeterForwardeObj");
    }

    public static void returnedSize(String method, String uri, int statusCode, int size) {
        PROMETHUS.responseSize(method, uri, statusCode, size);
        MICROMETER.responseSize(method, uri, statusCode, size);
    }

    protected abstract Object forwardStart(String method, String uri);

    protected abstract void requestEnd(String method, String uri, int statusCode, Object startObj, Object forwardObj);

    protected abstract void requestSize(String method, String uri, int size);

    protected abstract Object requestStart(String method, String uri);

    protected abstract void responseSize(String method, String uri, int statusCode, int size);

}
