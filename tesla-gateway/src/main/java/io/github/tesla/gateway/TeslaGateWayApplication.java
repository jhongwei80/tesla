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
package io.github.tesla.gateway;

import static com.hazelcast.util.EmptyStatement.ignore;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import io.github.tesla.gateway.metrics.MetricsHttpServer;
import io.github.tesla.gateway.netty.HttpFiltersSourceAdapter;
import io.github.tesla.gateway.netty.HttpProxyServer;

/**
 * @author liushiming
 * @version TeslaGateWayApplication.java, v 0.0.1 2018年1月24日 下午4:37:37 liushiming
 */
@ComponentScan(basePackages = {"io.github.tesla"})
@MapperScan(basePackages = {TeslaGateWayApplication.SCAN_PACKAGE}, annotationClass = Mapper.class)
@SpringBootApplication(exclude = {ValidationAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class TeslaGateWayApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeslaGateWayApplication.class);

    protected static final String SCAN_PACKAGE = "io.github.tesla.common";

    private static final int MAX_PORT = 100;

    static {
        System.setProperty("hazelcast.port", String.valueOf(getNextPort(5701)));
        System.setProperty("hazelcast.host", findHostRemoteIp());
        LOGGER.info(
            "[JVM] " + ManagementFactory.getRuntimeMXBean().getInputArguments().stream().collect(Collectors.joining()));
    }

    private static String findHostRemoteIp() {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses())) {
                    if (inetAddress instanceof Inet4Address) {
                        if (!"127.0.0.1".equals(inetAddress.getHostAddress())) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

    private static int getNextPort(int start) {
        for (int port = start; port < start + MAX_PORT; port++) {
            try {
                new ServerSocket(port).close();
                return port;
            } catch (IOException portInUse) {
                ignore(portInUse);
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SpringApplication.run(TeslaGateWayApplication.class, args);
    }

    @Value("${server.port}")
    private int httpPort;

    @Override
    public void run(String... arg0) throws Exception {
        runPrometheusServer();
        runNettyServer();
    }

    private void runNettyServer() {
        HttpProxyServer.bootstrap()//
            .withPort(httpPort)//
            .withFiltersSource(new HttpFiltersSourceAdapter())//
            .withAllowRequestToOriginServer(true)//
            .withAllowLocalOnly(false)//
            .start();
    }

    private void runPrometheusServer() throws IOException {
        final int metricePort = httpPort + 1;
        try {
            new MetricsHttpServer(metricePort, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
