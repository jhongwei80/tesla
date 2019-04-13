package io.github.tesla.gateway.netty.transmit.support;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public interface HostResolver {
    public InetSocketAddress resolve(String host, int port) throws UnknownHostException;
}
