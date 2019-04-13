package io.github.tesla.filter.utils;

import java.net.*;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;

public class NetworkUtil {

    public static Boolean equalAddress(InetSocketAddress address, String hostAndPort) {
        if (StringUtils.startsWithAny(hostAndPort, "localhost", "127.0.0.1")) {
            return true;
        } else {
            String addressStr = address.getHostName() + ":" + address.getPort();
            return addressStr.equals(hostAndPort) || addressStr.equals(hostAndPort);
        }
    }

    public static InetAddress firstLocalNonLoopbackIpv4Address() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isUp()) {
                    for (InterfaceAddress ifAddress : networkInterface.getInterfaceAddresses()) {
                        if (ifAddress.getNetworkPrefixLength() > 0 && ifAddress.getNetworkPrefixLength() <= 32
                            && !ifAddress.getAddress().isLoopbackAddress()) {
                            return ifAddress.getAddress();
                        }
                    }
                }
            }
            return null;
        } catch (SocketException se) {
            return null;
        }
    }

    public static InetAddress getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

}
