package cn.heshiqian.hotpothttp.core.factory;

import cn.heshiqian.hotpothttp.core.pojo.CacheFilePojo;
import cn.heshiqian.hotpothttp.core.pojo.Package;
import cn.heshiqian.hotpothttp.core.request.HttpImpl;
import cn.heshiqian.hotpothttp.core.response.HttpSend;
import cn.heshiqian.hotpothttp.core.response.HttpSendImpl;
import cn.heshiqian.hotpothttp.core.response.Response;
import cn.heshiqian.hotpothttp.core.response.ResponseProcessor;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static cn.heshiqian.hotpothttp.core.response.ResponseHelper.hphResponsePT;

public class ResponseBackFactory implements ResponseProcessor {

    @Override
    public HttpSend beforeSend(Response response) {
        int status = response.getStatus();
        if (status==404)
            return send404();
        if (status==500)
            return send500();

        String content = response.getContent();
        String characterEncoding = response.getCharacterEncoding();
        if (characterEncoding==null) characterEncoding="UTF-8";
        Map<String, String> headers = response.getHeaders();
        InputStream stream = response.getStream();
        Package<CacheFilePojo> aPackage = response.getPackage(hphResponsePT);
        CacheFilePojo cacheFilePojo = aPackage.getOwnObj();
        String contentType = headers.get("Content-Type");
//        headers.put("Content-Type",contentType+"; "+characterEncoding);
        headers.put("Content-Type",contentType);
        HttpSendImpl httpSend = new HttpSendImpl();
        httpSend.setHttp("HTTP/1.1 "+response.getStatus()+" \r\n");
        httpSend.setHeader(convertMapToHeader(headers)+"\r\n");
        if (stream!=null)
            httpSend.setStream(stream);
        if (cacheFilePojo.getBytes()!=null)
            httpSend.setPayload(cacheFilePojo.getBytes());
        if (content!=null)
            httpSend.setPayload(content.getBytes(Charset.forName(characterEncoding)));
        return httpSend;
    }

    private String convertMapToHeader(Map<String, String> headers) {
        StringBuilder sb=new StringBuilder();
        Set<Map.Entry<String, String>> entries = headers.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String headerKey = entry.getKey();
            String headerValue = entry.getValue();
            sb.append(headerKey).append(":").append(headerValue).append("\r\n");
        }
        sb.delete(sb.lastIndexOf("\r"),sb.lastIndexOf("\n"));
        return sb.toString();
    }

    private HttpSend send404(){
        HttpSendImpl httpSend = new HttpSendImpl();
        httpSend.setHttp(TCP_HEAD_404);
        httpSend.setHeader(HTTP_HEAD_404);
        httpSend.setPayload(HTTP_CONTENT_404.getBytes());
        return httpSend;
    }

    private HttpSend send500(){
        HttpSendImpl httpSend = new HttpSendImpl();
        httpSend.setHttp(TCP_HEAD_500);
        httpSend.setHeader(HTTP_HEAD_500);
        httpSend.setPayload(HTTP_CONTENT_500.getBytes());
        return httpSend;
    }


}
