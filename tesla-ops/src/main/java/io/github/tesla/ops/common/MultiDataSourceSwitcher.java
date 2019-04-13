package io.github.tesla.ops.common;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.google.common.collect.Maps;

public class MultiDataSourceSwitcher extends AbstractRoutingDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiDataSourceSwitcher.class);

    private static final ThreadLocal<String> dataSourceKey = new ThreadLocal<String>();

    private static Map<String, String> multiDataSourceNames = Maps.newConcurrentMap();

    public static void clearDataSourceType() {
        LOGGER.debug("thread:{},remove,dataSource:{}", Thread.currentThread().getName());
        dataSourceKey.remove();
    }

    public static Map<String, String> getMultiDataSourceNames() {
        return multiDataSourceNames;
    }

    public void setMultiDataSourceNames(Map<String, String> multiDataSourceNames) {
        MultiDataSourceSwitcher.multiDataSourceNames = multiDataSourceNames;
    }

    public static void setDataSourceKey(String dataSource) {
        LOGGER.debug("thread:{},set,dataSource:{}", Thread.currentThread().getName(), dataSource);
        dataSourceKey.set(dataSource);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String s = dataSourceKey.get();
        LOGGER.debug("thread:{},determine,dataSource:{}", Thread.currentThread().getName(), s);
        return s;
    }
}
