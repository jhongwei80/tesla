package io.github.tesla.gateway.metrics;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.bkjk.platform.monitor.Monitors;

/**
 * @Program: tesla
 * @Description:
 * @Author: shaoze.wang
 * @Create: 2018/11/27 14:17
 **/
public class MicrometerMetricsExporter extends MetricsExporter {

    public final static String PREFIX = "tesla.";
    public final static String REQUESTS_TOTAL = PREFIX + "requests.total";
    public final static String IN_PROGRESS_REQUESTS = PREFIX + "inprogress.requests";
    public final static String REQUESTS_LATENCY = PREFIX + "requests.latency";
    public final static String REQUESTS_FLOW_LATENCY = PREFIX + "requests.forward.latency";
    public final static String REQUEST_SIZE = PREFIX + "request.size";
    public final static String RESPONSE_SIZE = PREFIX + "response.size";
    public final static String METHOD = "method";
    public final static String URI = "uri";
    public final static String STATUS = "status";
    public final static String STATUS_1XX = "1XX";
    public final static String STATUS_2XX = "2XX";
    public final static String STATUS_3XX = "3XX";
    public final static String STATUS_4XX = "4XX";
    public final static String STATUS_5XX = "5XX";
    public final static String STATUS_XXX = "XXX";

    private static long[] SLA_MILLI_SECOND =
        new long[] {5, 10, 25, 50, 75, 100, 250, 500, 750, 1000, 2500, 5000, 7500, 10_000};
    private static double[] PERCENTILES = new double[] {0.1, 0.5, 0.9, 0.99};
    private static final Duration[] SLA = new Duration[SLA_MILLI_SECOND.length];

    static {
        for (int i = 0; i < SLA_MILLI_SECOND.length; i++) {
            SLA[i] = Duration.ofMillis(SLA_MILLI_SECOND[i]);
        }
    }

    private String convertUrl(String url) {
        String uri = StringUtils.substringBetween(url, "/", "/");
        return uri != null ? uri : "none";
    }

    @Override
    protected Object forwardStart(String method, String uri) {
        long now = Monitors.monotonicTime();
        return now;
    }

    private String getStatus(int statusCode) {
        if (statusCode >= 100 && statusCode < 200) {
            return STATUS_1XX;
        } else if (statusCode < 300) {
            return STATUS_2XX;
        } else if (statusCode < 400) {
            return STATUS_3XX;
        } else if (statusCode < 500) {
            return STATUS_4XX;
        } else if (statusCode < 600) {
            return STATUS_5XX;
        }
        return STATUS_XXX;
    }

    @Override
    public void requestEnd(String method, String uri, int statusCode, Object startTime, Object forwardTime) {
        if (startTime == null) {
            return;
        }
        uri = this.convertUrl(uri);
        long now = Monitors.monotonicTime();
        long timeElapsedNanosFromStart = now - (Long)startTime;
        if (forwardTime != null) {
            long noww = Monitors.monotonicTime();
            long timeElapsedNanosFromForward = noww - (Long)forwardTime;
            Monitors.recordTime(REQUESTS_FLOW_LATENCY, timeElapsedNanosFromForward, TimeUnit.NANOSECONDS, PERCENTILES,
                SLA, METHOD, method, URI, uri, STATUS, getStatus(statusCode));
        }
        Monitors.count(IN_PROGRESS_REQUESTS, -1, METHOD, method, URI, uri);
        Monitors.recordTime(REQUESTS_LATENCY, timeElapsedNanosFromStart, TimeUnit.NANOSECONDS, PERCENTILES, SLA, METHOD,
            method, URI, uri, STATUS, getStatus(statusCode));
    }

    @Override
    public void requestSize(String method, String uri, int size) {
        Monitors.summary(REQUEST_SIZE, size, PERCENTILES, METHOD, method, URI, uri);
    }

    @Override
    public Long requestStart(String method, String uri) {
        uri = this.convertUrl(uri);
        Monitors.count(IN_PROGRESS_REQUESTS, METHOD, method, URI, uri);
        Monitors.count(REQUESTS_TOTAL, METHOD, method, URI, uri);
        long now = Monitors.monotonicTime();
        return now;
    }

    @Override
    public void responseSize(String method, String uri, int statusCode, int size) {
        Monitors.summary(RESPONSE_SIZE, size, PERCENTILES, METHOD, method, URI, uri, STATUS, getStatus(statusCode));
    }

}
