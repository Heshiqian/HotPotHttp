package cn.heshiqian.hotpothttp.core.response;

import java.io.InputStream;

public interface HttpSend {

    String getHttp();

    String getHeader();

    byte[] getPayload();

    InputStream getStream();

    void setPayload(byte[] payload);
    void setHeader(String header);
    void setHttp(String http);
    void setStream(InputStream stream);
}
