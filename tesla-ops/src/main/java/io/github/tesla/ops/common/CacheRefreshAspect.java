package io.github.tesla.ops.common;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.tesla.common.dao.GatewayCacheRefreshMapper;
import io.github.tesla.common.domain.GatewayCacheRefreshDO;
import io.github.tesla.ops.utils.DateUtils;

@Aspect
@Component
public class CacheRefreshAspect {

    private Logger logger = LoggerFactory.getLogger(CacheRefreshAspect.class);

    @Pointcut("execution(public * io.github.tesla.common.dao.*.*(..)) && !execution( * io.github.tesla.common.dao.GatewayCacheRefreshMapper.*(..))  ")
    public void cacheRefreshCut() {}

    @Autowired
    private GatewayCacheRefreshMapper cacheRefreshMapper;

    @AfterReturning("cacheRefreshCut()")
    public void afterReturningMethod(JoinPoint jp) throws Throwable {

        String methodName = jp.getSignature().getName();
        methodName = methodName.toLowerCase();
        if (methodName.contains("insert") || methodName.contains("update") || methodName.contains("delete")) {
            List<GatewayCacheRefreshDO> gatewayCacheRefreshDOS = cacheRefreshMapper.selectByMap(null);
            if (gatewayCacheRefreshDOS.size() != 1) {
                throw new RuntimeException("刷新时间错误");
            }
            gatewayCacheRefreshDOS.get(0).setCacheModifyDate(DateUtils.getTimestampNow());
            cacheRefreshMapper.updateById(gatewayCacheRefreshDOS.get(0));
            logger.info("execute {},then refresh the cache time", jp);
        } else {
            logger.info("execute {},do not refresh the cache time", jp);
        }
    }

}
