package io.github.tesla.auth.server;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@ComponentScan(basePackages = {"io.github.tesla"})
@MapperScan(basePackages = {"io.github.tesla"}, annotationClass = Mapper.class)
@SpringBootApplication
public class TeslaOauth2Application {

    public static void main(String[] args) {
        SpringApplication.run(TeslaOauth2Application.class, args);
    }

}
