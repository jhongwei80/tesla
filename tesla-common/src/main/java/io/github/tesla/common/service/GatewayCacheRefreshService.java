package io.github.tesla.common.service;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.tesla.common.dao.GatewayCacheRefreshMapper;

@Service
public class GatewayCacheRefreshService {

    private static final String TIME_MAP_KEY = "cacheRefreshTime";

    private Timestamp backUpTime;

    @Autowired
    private GatewayCacheRefreshMapper cacheRefreshMapper;

    private Timestamp loadLastModifyTime() {
        Timestamp lastModifyTime = cacheRefreshMapper.selectByMap(null).get(0).getCacheModifyDate();
        return lastModifyTime;
    }

    public boolean isRefreshCache(Map<String, Timestamp> cacheRefreshTime) {
        Timestamp lastModifyTime = this.loadLastModifyTime();
        if (backUpTime == null || backUpTime.compareTo(lastModifyTime) != 0) {
            backUpTime = lastModifyTime;
        }
        Timestamp cacheModifyTime = cacheRefreshTime.get(TIME_MAP_KEY);
        return lastModifyTime == null || cacheModifyTime == null || cacheModifyTime.compareTo(lastModifyTime) != 0;
    }

    public void updateRefreshTime(Map<String, Timestamp> cacheRefreshTime) {
        if (backUpTime != null)
            cacheRefreshTime.put(TIME_MAP_KEY, backUpTime);
        else
            cacheRefreshTime.clear();
    }
}
