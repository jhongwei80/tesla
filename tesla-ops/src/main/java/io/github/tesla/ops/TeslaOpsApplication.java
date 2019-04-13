package io.github.tesla.ops;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@ComponentScan(basePackages = {"io.github.tesla.ops", "io.github.tesla.common"})
@MapperScan(basePackages = {TeslaOpsApplication.SCAN_PACKAGE_COMMON, TeslaOpsApplication.SCAN_PACKAGE_OPS},
    annotationClass = Mapper.class)
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class TeslaOpsApplication {
    protected static final String SCAN_PACKAGE_COMMON = "io.github.tesla.common";
    protected static final String SCAN_PACKAGE_OPS = "io.github.tesla.ops";

    public static void main(String[] args) {
        SpringApplication.run(TeslaOpsApplication.class, args);
    }

}
