package io.github.tesla.ops.system.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.tesla.ops.common.BaseController;

@Controller
@RequestMapping("/sys/nodemonitor")
public class NodeMonitorPageController extends BaseController {

    private String prefix = "system/nodemonitor";

    @RequiresPermissions("sys:nodemonitor:list")
    @GetMapping("/list")
    public String list() {
        return prefix + "/list";
    }

    @RequiresPermissions("sys:nodemonitor:list")
    @GetMapping("/redirectgateway")
    public void redirectGateway(@RequestParam("url") String url, HttpServletResponse response) {
        HttpURLConnection conn = null;
        try {
            URL path = new URL(url);
            conn = (HttpURLConnection)path.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            Map<String, List<String>> headerFields = conn.getHeaderFields();
            // 通过输入流获取数据
            InputStream fis = conn.getInputStream();
            // 清空response
            response.reset();
            // 设置response的Header
            headerFields.entrySet().forEach(header -> {
                response.addHeader(header.getKey(), header.getValue().get(0));
            });
            OutputStream toClient = response.getOutputStream();
            response.setContentType(conn.getContentType());
            writeStream(fis, toClient);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void writeStream(InputStream fis, OutputStream outStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = fis.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        fis.close();
    }
}
