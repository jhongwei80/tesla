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
package io.github.tesla.gateway.protocol.grpc;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;

import io.github.tesla.common.proto.ServiceResolver;
import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.endpoint.definition.GRpcRoutingDefinition;
import io.github.tesla.gateway.cache.FilterCache;

/**
 * @author liushiming
 * @version GrpcRouteService.java, v 0.0.1 2018年1月7日 下午12:59:14 liushiming
 */

public class ProtobufUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ProtobufUtil.class);

    private static Pair<Descriptor, Descriptor> findDirectyprotobuf(final GRpcRoutingDefinition definition) {
        FilterCache cacheComponent = SpringContextHolder.getBean(FilterCache.class);

        byte[] protoContent = cacheComponent.loadFileBytes(definition.getProtoFileId());
        FileDescriptorSet descriptorSet = null;
        if (protoContent != null && protoContent.length > 0) {
            try {
                descriptorSet = FileDescriptorSet.parseFrom(protoContent);
                ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(descriptorSet);
                ProtoMethodName protoMethodName = ProtoMethodName
                    .parseFullGrpcMethodName(definition.getServiceName() + "/" + definition.getMethodName());
                MethodDescriptor protoMethodDesc =
                    serviceResolver.resolveServiceMethod(protoMethodName.getServiceName(),
                        protoMethodName.getMethodName(), protoMethodName.getPackageName());
                return new ImmutablePair<Descriptor, Descriptor>(protoMethodDesc.getInputType(),
                    protoMethodDesc.getOutputType());
            } catch (InvalidProtocolBufferException e) {
                LOG.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static Pair<Descriptor, Descriptor> resolveServiceInputOutputType(final GRpcRoutingDefinition definition) {
        return findDirectyprotobuf(definition);
    }

}
