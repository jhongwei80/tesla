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
package io.github.tesla.gateway.config.eureka;

import java.util.Objects;

import com.netflix.discovery.shared.transport.EurekaTransportConfig;

/**
 * @author liushiming
 * @version CloudEurekaTransportConfig.java, v 0.0.1 2018年5月6日 上午10:33:59 liushiming
 */
public class CloudEurekaTransportConfig implements EurekaTransportConfig {

    private int sessionedClientReconnectIntervalSeconds = 20 * 60;

    private double retryableClientQuarantineRefreshPercentage = 0.66;

    private int bootstrapResolverRefreshIntervalSeconds = 5 * 60;

    private int applicationsResolverDataStalenessThresholdSeconds = 5 * 60;

    private int asyncResolverRefreshIntervalMs = 5 * 60 * 1000;

    private int asyncResolverWarmUpTimeoutMs = 5000;

    private int asyncExecutorThreadPoolSize = 5;

    private String readClusterVip;

    private String writeClusterVip;

    private boolean bootstrapResolverForQuery = true;

    private String bootstrapResolverStrategy;

    private boolean applicationsResolverUseIp = false;

    @Override
    public boolean applicationsResolverUseIp() {
        return this.applicationsResolverUseIp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CloudEurekaTransportConfig that = (CloudEurekaTransportConfig)o;
        return sessionedClientReconnectIntervalSeconds == that.sessionedClientReconnectIntervalSeconds
            && Double.compare(retryableClientQuarantineRefreshPercentage,
                that.retryableClientQuarantineRefreshPercentage) == 0
            && bootstrapResolverRefreshIntervalSeconds == that.bootstrapResolverRefreshIntervalSeconds
            && applicationsResolverDataStalenessThresholdSeconds == that.applicationsResolverDataStalenessThresholdSeconds
            && asyncResolverRefreshIntervalMs == that.asyncResolverRefreshIntervalMs
            && asyncResolverWarmUpTimeoutMs == that.asyncResolverWarmUpTimeoutMs
            && asyncExecutorThreadPoolSize == that.asyncExecutorThreadPoolSize
            && Objects.equals(readClusterVip, that.readClusterVip)
            && Objects.equals(writeClusterVip, that.writeClusterVip)
            && bootstrapResolverForQuery == that.bootstrapResolverForQuery
            && Objects.equals(bootstrapResolverStrategy, that.bootstrapResolverStrategy)
            && applicationsResolverUseIp == that.applicationsResolverUseIp;
    }

    @Override
    public int getApplicationsResolverDataStalenessThresholdSeconds() {
        return applicationsResolverDataStalenessThresholdSeconds;
    }

    @Override
    public int getAsyncExecutorThreadPoolSize() {
        return asyncExecutorThreadPoolSize;
    }

    @Override
    public int getAsyncResolverRefreshIntervalMs() {
        return asyncResolverRefreshIntervalMs;
    }

    @Override
    public int getAsyncResolverWarmUpTimeoutMs() {
        return asyncResolverWarmUpTimeoutMs;
    }

    public int getBootstrapResolverRefreshIntervalSeconds() {
        return bootstrapResolverRefreshIntervalSeconds;
    }

    @Override
    public String getBootstrapResolverStrategy() {
        return bootstrapResolverStrategy;
    }

    @Override
    public String getReadClusterVip() {
        return readClusterVip;
    }

    @Override
    public double getRetryableClientQuarantineRefreshPercentage() {
        return retryableClientQuarantineRefreshPercentage;
    }

    @Override
    public int getSessionedClientReconnectIntervalSeconds() {
        return sessionedClientReconnectIntervalSeconds;
    }

    @Override
    public String getWriteClusterVip() {
        return writeClusterVip;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionedClientReconnectIntervalSeconds, retryableClientQuarantineRefreshPercentage,
            bootstrapResolverRefreshIntervalSeconds, applicationsResolverDataStalenessThresholdSeconds,
            asyncResolverRefreshIntervalMs, asyncResolverWarmUpTimeoutMs, asyncExecutorThreadPoolSize, readClusterVip,
            writeClusterVip, bootstrapResolverForQuery, bootstrapResolverStrategy, applicationsResolverUseIp);
    }

    public boolean isApplicationsResolverUseIp() {
        return applicationsResolverUseIp;
    }

    public boolean isBootstrapResolverForQuery() {
        return bootstrapResolverForQuery;
    }

    public void
        setApplicationsResolverDataStalenessThresholdSeconds(int applicationsResolverDataStalenessThresholdSeconds) {
        this.applicationsResolverDataStalenessThresholdSeconds = applicationsResolverDataStalenessThresholdSeconds;
    }

    public void setApplicationsResolverUseIp(boolean applicationsResolverUseIp) {
        this.applicationsResolverUseIp = applicationsResolverUseIp;
    }

    public void setAsyncExecutorThreadPoolSize(int asyncExecutorThreadPoolSize) {
        this.asyncExecutorThreadPoolSize = asyncExecutorThreadPoolSize;
    }

    public void setAsyncResolverRefreshIntervalMs(int asyncResolverRefreshIntervalMs) {
        this.asyncResolverRefreshIntervalMs = asyncResolverRefreshIntervalMs;
    }

    public void setAsyncResolverWarmUpTimeoutMs(int asyncResolverWarmUpTimeoutMs) {
        this.asyncResolverWarmUpTimeoutMs = asyncResolverWarmUpTimeoutMs;
    }

    public void setBootstrapResolverForQuery(boolean bootstrapResolverForQuery) {
        this.bootstrapResolverForQuery = bootstrapResolverForQuery;
    }

    public void setBootstrapResolverRefreshIntervalSeconds(int bootstrapResolverRefreshIntervalSeconds) {
        this.bootstrapResolverRefreshIntervalSeconds = bootstrapResolverRefreshIntervalSeconds;
    }

    public void setBootstrapResolverStrategy(String bootstrapResolverStrategy) {
        this.bootstrapResolverStrategy = bootstrapResolverStrategy;
    }

    public void setReadClusterVip(String readClusterVip) {
        this.readClusterVip = readClusterVip;
    }

    public void setRetryableClientQuarantineRefreshPercentage(double retryableClientQuarantineRefreshPercentage) {
        this.retryableClientQuarantineRefreshPercentage = retryableClientQuarantineRefreshPercentage;
    }

    public void setSessionedClientReconnectIntervalSeconds(int sessionedClientReconnectIntervalSeconds) {
        this.sessionedClientReconnectIntervalSeconds = sessionedClientReconnectIntervalSeconds;
    }

    public void setWriteClusterVip(String writeClusterVip) {
        this.writeClusterVip = writeClusterVip;
    }

    @Override
    public String toString() {
        return new StringBuilder("CloudEurekaTransportConfig{").append("sessionedClientReconnectIntervalSeconds=")
            .append(sessionedClientReconnectIntervalSeconds).append(", ")
            .append("retryableClientQuarantineRefreshPercentage=").append(retryableClientQuarantineRefreshPercentage)
            .append(", ").append("bootstrapResolverRefreshIntervalSeconds=")
            .append(bootstrapResolverRefreshIntervalSeconds).append(", ")
            .append("applicationsResolverDataStalenessThresholdSeconds=")
            .append(applicationsResolverDataStalenessThresholdSeconds).append(", ")
            .append("asyncResolverRefreshIntervalMs=").append(asyncResolverRefreshIntervalMs).append(", ")
            .append("asyncResolverWarmUpTimeoutMs=").append(asyncResolverWarmUpTimeoutMs).append(", ")
            .append("asyncExecutorThreadPoolSize=").append(asyncExecutorThreadPoolSize).append(", ")
            .append("readClusterVip='").append(readClusterVip).append("', ").append("writeClusterVip='")
            .append(writeClusterVip).append("', ").append("bootstrapResolverForQuery=")
            .append(bootstrapResolverForQuery).append(", ").append("bootstrapResolverStrategy='")
            .append(bootstrapResolverStrategy).append("', ").append("applicationsResolverUseIp=")
            .append(applicationsResolverUseIp).append(", ").append("}").toString();
    }

    @Override
    public boolean useBootstrapResolverForQuery() {
        return this.bootstrapResolverForQuery;
    }

}
