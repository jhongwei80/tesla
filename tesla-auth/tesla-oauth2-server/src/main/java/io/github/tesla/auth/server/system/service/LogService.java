package io.github.tesla.auth.server.system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.tesla.auth.server.system.dao.LogDao;
import io.github.tesla.auth.server.system.domain.LogDO;
import io.github.tesla.auth.server.system.domain.PageDO;
import io.github.tesla.auth.server.utils.Query;

@Service
public class LogService {

    @Autowired
    private LogDao logMapper;

    public PageDO<LogDO> queryList(Query query) {
        int total = logMapper.count(query);
        List<LogDO> logs = logMapper.list(query);
        PageDO<LogDO> page = new PageDO<>();
        page.setTotal(total);
        page.setRows(logs);
        return page;
    }

    public boolean remove(Long id) {
        return logMapper.remove(id) == 1;
    }

    public boolean batchRemove(Long[] ids) {
        return logMapper.batchRemove(ids) > 0;
    }
}
