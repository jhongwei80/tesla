package io.github.tesla.gateway.protocol.grpc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.tuple.Pair;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;

import io.github.saluki.grpc.exception.RpcFrameworkException;
import io.github.saluki.grpc.exception.RpcServiceException;
import io.github.tesla.filter.endpoint.definition.GRpcRoutingDefinition;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.grpc.MethodDescriptor;
import io.grpc.MethodDescriptor.Marshaller;
import io.grpc.MethodDescriptor.MethodType;
import io.netty.util.CharsetUtil;

/**
 * @author liushiming
 * @version DynamicGrpcClient1.java, v 0.0.1 2018年1月5日 下午5:38:46 liushiming
 */
public class DynamicGrpcClient {

    protected class DynamicMessageMarshaller implements Marshaller<DynamicMessage> {
        private final Descriptor messageDescriptor;

        public DynamicMessageMarshaller(Descriptor messageDescriptor) {
            this.messageDescriptor = messageDescriptor;
        }

        @Override
        public DynamicMessage parse(InputStream inputStream) {
            try {
                return DynamicMessage.newBuilder(messageDescriptor)
                    .mergeFrom(inputStream, ExtensionRegistryLite.getEmptyRegistry()).build();
            } catch (IOException e) {
                throw new RuntimeException("Unable to merge from the supplied input stream", e);
            }
        }

        @Override
        public InputStream stream(DynamicMessage abstractMessage) {
            return abstractMessage.toByteString().newInput();
        }
    }

    private static final JsonFormat JSON2PROTOBUF = new JsonFormat();

    static {
        JSON2PROTOBUF.setDefaultCharset(CharsetUtil.UTF_8);
    }

    private final io.github.saluki.grpc.service.GenericService genricService;

    public DynamicGrpcClient(io.github.saluki.grpc.service.GenericService genricService) {
        super();
        this.genricService = genricService;
    }

    private DynamicMessage createGrpcDynamicMessage(final Descriptor messageDefine, String json) throws IOException {
        DynamicMessage.Builder messageBuilder = DynamicMessage.newBuilder(messageDefine);
        JSON2PROTOBUF.merge(new ByteArrayInputStream(json.getBytes()), messageBuilder);
        return messageBuilder.build();
    }

    private MethodDescriptor<DynamicMessage, DynamicMessage> createGrpcMethodDescriptor(String serviceName,
        String methodName, Descriptor inPutType, Descriptor outPutType) {
        String fullMethodName = MethodDescriptor.generateFullMethodName(serviceName, methodName);
        return io.grpc.MethodDescriptor.<DynamicMessage, DynamicMessage>newBuilder().setType(MethodType.UNARY)
            .setFullMethodName(fullMethodName).setRequestMarshaller(new DynamicMessageMarshaller(inPutType))
            .setResponseMarshaller(new DynamicMessageMarshaller(outPutType)).setSafe(false).setIdempotent(false)
            .build();
    }

    public String doRpcRemoteCall(final GRpcRoutingDefinition definition,
        final NettyHttpServletRequest servletRequest) {
        String outPutJson = null;
        try {
            final byte[] bodyContent = servletRequest.getRequestBody();
            outPutJson = new String(bodyContent, CharsetUtil.UTF_8);;
            final String serviceName = definition.getServiceName();
            final String methodName = definition.getMethodName();
            final String group = definition.getGroup();
            final String version = definition.getVersion();
            Pair<Descriptor, Descriptor> inOutType = ProtobufUtil.resolveServiceInputOutputType(definition);
            Descriptor inPutType = inOutType.getLeft();
            Descriptor outPutType = inOutType.getRight();
            MethodDescriptor<DynamicMessage, DynamicMessage> methodDesc =
                this.createGrpcMethodDescriptor(serviceName, methodName, inPutType, outPutType);
            DynamicMessage message = this.createGrpcDynamicMessage(inPutType, outPutJson);
            Message response =
                (Message)genricService.$invoke(serviceName, group, version, methodName, methodDesc, message);
            return JSON2PROTOBUF.printToString(response);
        } catch (IOException e) {
            throw new RpcServiceException(String.format(
                "json covert to DynamicMessage failed! the json is :%s, the protobuf type is: %s", outPutJson), e);
        } catch (Throwable e) {
            throw new RpcFrameworkException(String.format(
                "service definition is wrong,please check the proto file you update,service is %s, method is %s",
                definition.getServiceName(), definition.getMethodName()), e);
        }
    }

}
