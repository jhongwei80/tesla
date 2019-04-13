package io.github.tesla.gateway.metrics.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.HttpHeaderNames;

/**
 * @author: zhangzhiping
 * @date: 2019/2/19 10:52
 * @description:
 */
public class ThreadDumpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Map<String, Object> threadMap = Maps.newHashMap();
        threadMap.put("threads", Arrays.asList(ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)));
        String threadDump = JsonUtils.formatJson(JsonUtils.serializeToJson(threadMap));
        String date = (new SimpleDateFormat("yyyy-MM-dd-HH-mm")).format(new Date());
        String fileName = "threaddump" + date + ".json";
        OutputStream os = httpExchange.getResponseBody();
        httpExchange.getResponseHeaders().add(HttpHeaderNames.CONTENT_DISPOSITION.toString(),
            "attachment; filename=" + fileName);
        httpExchange.getResponseHeaders().add(HttpHeaderNames.CONTENT_TYPE.toString(), "application/octet-stream");
        httpExchange.sendResponseHeaders(200, threadDump.getBytes().length);
        os.write(threadDump.getBytes());
        os.flush();
        os.close();
    }
}
