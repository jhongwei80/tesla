package io.github.tesla.backend;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import io.github.tesla.backend.dubbo.EmbeddedZooKeeper;

@SpringBootApplication
@EnableEurekaClient
public class TeslaBackendSample {
    public static void main(String[] args) {
        new SpringApplicationBuilder(TeslaBackendSample.class)
            .listeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>)event -> {
                Environment environment = event.getEnvironment();
                int port = environment.getProperty("embedded.zookeeper.port", int.class);
                new EmbeddedZooKeeper(port, false).start();
            }).run(args);
    }

}
