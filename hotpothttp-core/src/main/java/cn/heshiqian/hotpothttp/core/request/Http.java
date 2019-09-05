package cn.heshiqian.hotpothttp.core.request;

import java.net.InetAddress;

public interface Http {

    Method method();
    String version();
    String path();
    String parameter();
    String[] head();
    InetAddress address();

    enum Method{
        GET,POST
    }
}
