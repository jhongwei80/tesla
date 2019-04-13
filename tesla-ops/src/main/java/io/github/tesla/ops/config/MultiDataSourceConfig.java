package io.github.tesla.ops.config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceProperties;
import com.google.common.collect.Maps;

import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.ops.common.MultiDataSourceSwitcher;

@Configuration
public class MultiDataSourceConfig {

    @Value("${tesla.datasource.url}")
    private String dataSourceUrl;

    @Value("${tesla.datasource.name}")
    private String dataSourceName;

    @Value("${tesla.datasource.username}")
    private String dataSourceUsername;

    @Value("${tesla.datasource.password}")
    private String dataSourcePassword;

    @Bean("multiDataSourceSwitcher")
    public MultiDataSourceSwitcher getDataSourceSwitcher() {
        MultiDataSourceSwitcher multiDataSourceSwitcher = new MultiDataSourceSwitcher();
        initMultiDataSource(multiDataSourceSwitcher);
        return multiDataSourceSwitcher;
    }

    public void initMultiDataSource(MultiDataSourceSwitcher multiDataSourceSwitcher) {
        if (JsonUtils.isJson(dataSourceUrl)) {
            initMultiDataSourceWithMapConfig(multiDataSourceSwitcher);
        } else {
            initMultiDataSourceWithStringConfig(multiDataSourceSwitcher);
        }
    }

    public void initMultiDataSourceWithStringConfig(MultiDataSourceSwitcher multiDataSourceSwitcher) {
        Map<Object, Object> multiDataSources = Maps.newConcurrentMap();
        Map<String, String> multiDataSourceShowNames = Maps.newConcurrentMap();
        DruidDataSourceProperties properties = new DruidDataSourceProperties();
        properties.setUrl(dataSourceUrl);
        properties.setUsername(dataSourceUsername);
        properties.setPassword(dataSourcePassword);
        rewriteDruidDataSourceProperties(properties);
        DruidDataSource dataSource = build(properties);
        if (dataSource != null) {
            multiDataSources.put("local", dataSource);
        }
        multiDataSourceShowNames.put("local", dataSourceName);
        multiDataSourceSwitcher.setTargetDataSources(multiDataSources);
        multiDataSourceSwitcher.setMultiDataSourceNames(multiDataSourceShowNames);
        multiDataSourceSwitcher.setDefaultTargetDataSource(multiDataSources.get("local"));
    }

    public void initMultiDataSourceWithMapConfig(MultiDataSourceSwitcher multiDataSourceSwitcher) {
        Map<Object, Object> multiDataSources = Maps.newConcurrentMap();
        Map<String, String> multiDataSourceShowNames = Maps.newConcurrentMap();

        Map<String, String> multiDataSourceUrlMap = JsonUtils.fromJson(dataSourceUrl, HashMap.class);
        Map<String, String> multiDataSourceUsernameMap = JsonUtils.fromJson(dataSourceUsername, HashMap.class);
        Map<String, String> multiDataSourcePasswordMap = JsonUtils.fromJson(dataSourcePassword, HashMap.class);
        Map<String, String> multiDataSourceNameMap = JsonUtils.fromJson(dataSourceName, HashMap.class);

        multiDataSourceUrlMap.entrySet().forEach(entry -> {
            String configKey = entry.getKey();
            DruidDataSourceProperties properties = new DruidDataSourceProperties();
            properties.setUrl(multiDataSourceUrlMap.get(configKey));
            properties.setUsername(multiDataSourceUsernameMap.get(configKey));
            properties.setPassword(multiDataSourcePasswordMap.get(configKey));
            rewriteDruidDataSourceProperties(properties);
            DruidDataSource dataSource = build(properties);
            if (dataSource != null) {
                multiDataSources.put(configKey, dataSource);
            }
            multiDataSourceShowNames.put(configKey, multiDataSourceNameMap.get(configKey));
        });
        multiDataSourceSwitcher.setTargetDataSources(multiDataSources);
        multiDataSourceSwitcher.setMultiDataSourceNames(multiDataSourceShowNames);
        multiDataSourceSwitcher.setDefaultTargetDataSource(
            multiDataSources.getOrDefault("default", multiDataSources.values().toArray()[0]));
    }

    private void rewriteDruidDataSourceProperties(DruidDataSourceProperties properties) {

        if (properties.getDriverClassName() == null) {
            properties.setDriverClassName("com.mysql.jdbc.Driver");
        }

        if (properties.getInitialSize() == null) {
            properties.setInitialSize(10);
        }

        if (properties.getMinIdle() == null) {
            properties.setMinIdle(5);
        }

        if (properties.getMaxActive() == null) {
            properties.setMaxActive(100);
        }

        if (properties.getMaxWait() == null) {
            properties.setMaxWait(60000L);
        }

        if (properties.getTimeBetweenEvictionRunsMillis() == null) {
            properties.setTimeBetweenEvictionRunsMillis(60000L);
        }

        if (properties.getMinEvictableIdleTimeMillis() == null) {
            properties.setTimeBetweenEvictionRunsMillis(300000L);
        }

        if (properties.getValidationQuery() == null) {
            properties.setValidationQuery("SELECT 1 FROM DUAL");
        }

        if (properties.getTestWhileIdle() == null) {
            properties.setTestWhileIdle(true);
        }

        if (properties.getTestOnBorrow() == null) {
            properties.setTestOnBorrow(true);
        }

        if (properties.getTestOnReturn() == null) {
            properties.setTestOnReturn(false);
        }

        if (properties.getPoolPreparedStatements() == null) {
            properties.setPoolPreparedStatements(true);
        }

        if (properties.getMaxPoolPreparedStatementPerConnectionSize() == null) {
            properties.setMaxPoolPreparedStatementPerConnectionSize(30);
        }

    }

    DruidDataSource build(DruidDataSourceProperties properties) {
        DruidDataSource dataSource = new DruidDataSource();
        if (properties.getUrl() != null) {
            dataSource.setUrl(properties.getUrl());
        }
        if (properties.getUsername() != null) {
            dataSource.setUsername(properties.getUsername());
        }
        if (properties.getPassword() != null) {
            dataSource.setPassword(properties.getPassword());
        }
        if (properties.getDriverClassName() != null) {
            dataSource.setDriverClassName(properties.getDriverClassName());
        }
        if (properties.getInitialSize() != null) {
            dataSource.setInitialSize(properties.getInitialSize());
        }
        if (properties.getMaxActive() != null) {
            dataSource.setMaxActive(properties.getMaxActive());
        }
        if (properties.getMinIdle() != null) {
            dataSource.setMinIdle(properties.getMinIdle());
        }
        if (properties.getMaxWait() != null) {
            dataSource.setMaxWait(properties.getMaxWait());
        }
        if (properties.getPoolPreparedStatements() != null) {
            dataSource.setPoolPreparedStatements(properties.getPoolPreparedStatements());
        }
        if (properties.getMaxOpenPreparedStatements() != null) {
            dataSource.setMaxOpenPreparedStatements(properties.getMaxOpenPreparedStatements());
        }
        if (properties.getMaxPoolPreparedStatementPerConnectionSize() != null) {
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(
                properties.getMaxPoolPreparedStatementPerConnectionSize());
        }
        if (properties.getValidationQuery() != null) {
            dataSource.setValidationQuery(properties.getValidationQuery());
        }
        if (properties.getValidationQueryTimeout() != null) {
            dataSource.setValidationQueryTimeout(properties.getValidationQueryTimeout());
        }
        if (properties.getTestWhileIdle() != null) {
            dataSource.setTestWhileIdle(properties.getTestWhileIdle());
        }
        if (properties.getTestOnBorrow() != null) {
            dataSource.setTestOnBorrow(properties.getTestOnBorrow());
        }
        if (properties.getTestOnReturn() != null) {
            dataSource.setTestOnReturn(properties.getTestOnReturn());
        }
        if (properties.getTimeBetweenEvictionRunsMillis() != null) {
            dataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        }
        if (properties.getMinEvictableIdleTimeMillis() != null) {
            dataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        }
        if (properties.getMaxEvictableIdleTimeMillis() != null) {
            dataSource.setMaxEvictableIdleTimeMillis(properties.getMaxEvictableIdleTimeMillis());
        }
        try {
            if (properties.getFilters() != null) {
                dataSource.setFilters(properties.getFilters());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

}
