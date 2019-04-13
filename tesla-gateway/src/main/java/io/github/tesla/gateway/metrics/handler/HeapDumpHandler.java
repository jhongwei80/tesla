package io.github.tesla.gateway.metrics.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.PlatformManagedObject;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.netty.handler.codec.http.HttpHeaderNames;

/**
 * @author: zhangzhiping
 * @date: 2019/2/19 11:14
 * @description:
 */
public class HeapDumpHandler implements HttpHandler {
    private Object diagnosticMXBean;
    private Method dumpHeapMethod;

    private final long timeout;

    private final Lock lock = new ReentrantLock();

    public HeapDumpHandler() {
        try {
            Class<?> diagnosticMXBeanClass =
                ClassUtils.resolveClassName("com.sun.management.HotSpotDiagnosticMXBean", null);
            this.diagnosticMXBean =
                ManagementFactory.getPlatformMXBean((Class<PlatformManagedObject>)diagnosticMXBeanClass);
            this.dumpHeapMethod =
                ReflectionUtils.findMethod(diagnosticMXBeanClass, "dumpHeap", String.class, Boolean.TYPE);
            timeout = TimeUnit.SECONDS.toMillis(10);
        } catch (Throwable ex) {
            throw new RuntimeException("Unable to locate HotSpotDiagnosticMXBean", ex);
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            if (this.lock.tryLock(this.timeout, TimeUnit.MILLISECONDS)) {
                try {
                    File file = createTempFile();
                    dumpHeap(file, true);
                    FileInputStream fis = new FileInputStream(file);
                    OutputStream os = httpExchange.getResponseBody();
                    httpExchange.getResponseHeaders().add(HttpHeaderNames.CONTENT_DISPOSITION.toString(),
                        "attachment; filename=" + file.getName());
                    httpExchange.getResponseHeaders().add(HttpHeaderNames.CONTENT_TYPE.toString(),
                        "application/octet-stream");
                    httpExchange.sendResponseHeaders(200, file.length());
                    int byteSend = 0;
                    byte[] buff = new byte[512];
                    while ((byteSend = fis.read(buff)) != -1) {
                        os.write(buff, 0, byteSend);
                    }
                    os.flush();
                    os.close();
                } finally {
                    this.lock.unlock();
                }
            } else {
                httpExchange.sendResponseHeaders(429, 0);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException ex) {
            httpExchange.sendResponseHeaders(500, 0);
        }
    }

    private void dumpHeap(File file, boolean live) {
        ReflectionUtils.invokeMethod(this.dumpHeapMethod, this.diagnosticMXBean,
            new Object[] {file.getAbsolutePath(), live});
    }

    private File createTempFile() throws IOException {
        String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
        File file = File.createTempFile("heapdump" + date + "-", ".hprof");
        file.delete();
        return file;
    }

}
