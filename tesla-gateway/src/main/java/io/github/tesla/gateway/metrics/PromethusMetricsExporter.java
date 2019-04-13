/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.tesla.gateway.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;

/**
 * @author liushiming
 * @version PrometheusPublisher.java, v 0.0.1 2018年5月23日 下午3:53:26 liushiming
 */
public class PromethusMetricsExporter extends MetricsExporter {

    private static final Counter TOTAL_REQUEST_COUNTER =
        Counter.build().name("requests_total").help("Requests total").labelNames("method", "uri").register();
    private static final Gauge INPROGRESS_REQUESTS_GAUGE =
        Gauge.build().name("inprogress_requests").help("Inprogress Requests").labelNames("method", "uri").register();
    private static final Histogram REQUEST_LATENCY_HISTOGRAM = Histogram.build().labelNames("method")
        .name("requests_latency_seconds").help("Request latency in seconds.").labelNames("method", "uri").register();
    private static final Summary REQUEST_LATENCY_SUMMARY =
        Summary.build().quantile(0.1, 0.05).quantile(0.5, 0.05).quantile(0.9, 0.01).quantile(0.99, 0.001)
            .name("requests_latency").help("Request latency").labelNames("method", "uri").register();
    private static final Histogram FORWARD_LATENCY_HISTOGRAM = Histogram.build().labelNames("method")
        .name("forward_latency_seconds").help("forward latency in seconds.").labelNames("method", "uri").register();
    private static final Summary FORWARD_LATENCY_SUMMARY =
        Summary.build().quantile(0.1, 0.05).quantile(0.5, 0.05).quantile(0.9, 0.01).quantile(0.99, 0.001)
            .name("forwards_latency").help("forward latency").labelNames("method", "uri").register();
    private static final Summary REQUEST_SIZE_SUMMARY = Summary.build().quantile(0.1, 0.05).quantile(0.5, 0.05)
        .quantile(0.9, 0.01).quantile(0.99, 0.001).name("request_size").help("Request size").register();
    private static final Summary RESPONSE_SIZE_SUMMARY = Summary.build().quantile(0.1, 0.05).quantile(0.5, 0.05)
        .quantile(0.9, 0.01).quantile(0.99, 0.001).name("response_size").help("Response size").register();
    private static final Counter HTTP_1XX_COUNTER =
        Counter.build().name("http_1XX_requests_total").help("HTTP 1XX Status Codes").register();
    private static final Counter HTTP_2XX_COUNTER =
        Counter.build().name("http_2XX_requests_total").help("HTTP 2XX Status Codes").register();
    private static final Counter HTTP_3XX_COUNTER =
        Counter.build().name("http_3XX_requests_total").help("HTTP 3XX Status Codes").register();
    private static final Counter HTTP_4XX_COUNTER =
        Counter.build().name("http_4XX_requests_total").help("HTTP 4XX Status Codes").register();
    private static final Counter HTTP_5XX_COUNTER =
        Counter.build().name("http_5XX_requests_total").help("HTTP 5XX Status Codes").register();

    @Override
    protected Object forwardStart(String method, String uri) {
        return new Object[] {FORWARD_LATENCY_HISTOGRAM.labels(method, uri).startTimer(),
            FORWARD_LATENCY_SUMMARY.labels(method, uri).startTimer()};
    }

    @Override
    public void requestEnd(String method, String uri, int statusCode, Object startObj, Object forwardObj) {
        INPROGRESS_REQUESTS_GAUGE.labels(method, uri).dec();
        // 总耗时
        if (startObj != null) {
            Object[] startObjects = (Object[])startObj;
            if (startObjects[0] != null) {
                ((Histogram.Timer)startObjects[0]).observeDuration();
            }
            if (startObjects[1] != null) {
                ((Summary.Timer)startObjects[1]).observeDuration();
            }
        }
        // 转发耗时
        if (forwardObj != null) {
            Object[] forwardObjects = (Object[])forwardObj;
            ((Histogram.Timer)forwardObjects[0]).observeDuration();
            ((Summary.Timer)forwardObjects[1]).observeDuration();
        }
        if (statusCode >= 100 && statusCode < 200) {
            HTTP_1XX_COUNTER.inc();
        } else if (statusCode < 300) {
            HTTP_2XX_COUNTER.inc();
        } else if (statusCode < 400) {
            HTTP_3XX_COUNTER.inc();
        } else if (statusCode < 500) {
            HTTP_4XX_COUNTER.inc();
        } else if (statusCode < 600) {
            HTTP_5XX_COUNTER.inc();
        }
    }

    @Override
    public void requestSize(String method, String uri, int size) {
        REQUEST_SIZE_SUMMARY.observe(size);
    }

    @Override
    public Object requestStart(String method, String uri) {
        INPROGRESS_REQUESTS_GAUGE.labels(method, uri).inc();
        TOTAL_REQUEST_COUNTER.labels(method, uri).inc();
        return new Object[] {REQUEST_LATENCY_HISTOGRAM.labels(method, uri).startTimer(),
            REQUEST_LATENCY_SUMMARY.labels(method, uri).startTimer()};
    }

    @Override
    public void responseSize(String method, String uri, int statusCode, int size) {
        RESPONSE_SIZE_SUMMARY.observe(size);
    }

}
