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
package io.github.tesla.gateway.cache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liushiming
 * @version AbstractScheduleCache.java, v 0.0.1 2018年2月28日 上午10:27:30 liushiming
 */
public abstract class AbstractScheduleCache {
    private static class NamedThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        private final String mPrefix;

        private final boolean mDaemon;

        private final ThreadGroup mGroup;

        public NamedThreadFactory() {
            this("TeslaScheduleCache-" + POOL_SEQ.getAndIncrement(), true);
        }

        public NamedThreadFactory(String prefix, boolean daemon) {
            mPrefix = prefix + "-thread-";
            mDaemon = daemon;
            SecurityManager s = System.getSecurityManager();
            mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
        }

        @Override
        public Thread newThread(Runnable runnable) {
            String name = mPrefix + mThreadNum.getAndIncrement();
            Thread ret = new Thread(mGroup, runnable, name, 0);
            ret.setDaemon(mDaemon);
            return ret;
        }

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractScheduleCache.class);
    private static final int cpu = Runtime.getRuntime().availableProcessors();
    private static final ScheduledExecutorService SCHEDULE_EXCUTOR =
        Executors.newScheduledThreadPool(cpu, new NamedThreadFactory());

    private long INTERVAL = 60;

    protected abstract void doCache();

    @PostConstruct
    public void doSchedule() {
        init();
        SCHEDULE_EXCUTOR.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    doCache();
                } catch (Throwable e) {
                    LOGGER.error(e.getMessage(), e);
                }

            }
        }, 0, INTERVAL, TimeUnit.SECONDS);
    }

    protected abstract void init();

}
