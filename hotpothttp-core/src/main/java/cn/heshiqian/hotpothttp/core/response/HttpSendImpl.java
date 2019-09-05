package cn.heshiqian.hotpothttp.core.response;

import java.io.InputStream;

public class HttpSendImpl implements HttpSend {

    private String header;
    private String http;
    private byte[] payload;
    private InputStream stream;

    @Override
    public String getHttp() {
        return http;
    }

    @Override
    public String getHeader() {
        return header;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    @Override
    public InputStream getStream() {
        return stream;
    }

    @Override
    public void setPayload(byte[] payload) {
        this.payload=payload;
    }

    @Override
    public void setHeader(String header) {
        this.header=header;
    }

    @Override
    public void setHttp(String http) {
        this.http=http;
    }

    @Override
    public void setStream(InputStream stream) {
        this.stream=stream;
    }
}
