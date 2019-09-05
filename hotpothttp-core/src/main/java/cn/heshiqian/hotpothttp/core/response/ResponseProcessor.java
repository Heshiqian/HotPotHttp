package cn.heshiqian.hotpothttp.core.response;

import cn.heshiqian.hotpothttp.core.Version;

public interface ResponseProcessor {

    String TCP_HEAD_500="HTTP/1.1 500 "+"internal error".toUpperCase()+"\r\n";
    String TCP_HEAD_404="HTTP/1.1 404 "+"not found".toUpperCase()+"\r\n";
    String TCP_HEAD_200="HTTP/1.1 200 "+"ok".toUpperCase()+"\r\n";


    String HTTP_HEAD_500="Server:HotPotHttp_V"+ Version.version+"\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Connection: close\r\n";
    String HTTP_HEAD_404="Server:HotPotHttp_V"+ Version.version+"\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Connection: close\r\n";


    String HTTP_CONTENT_500="<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <title>HotPotHttp 500</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <h1>500 Internal Error</h1>\n" +
            "    <hr>\n" +
            "    <span style=\"font-style: italic\">HotPotHttp version:"+Version.version+"</span>\n" +
            "</body>\n" +
            "</html>";
    String HTTP_CONTENT_404="<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <title>HotPotHttp 404</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <h1>404 Not Found</h1>\n" +
            "    <hr>\n" +
            "    <span style=\"font-style: italic\">HotPotHttp version:"+Version.version+"</span>\n" +
            "</body>\n" +
            "</html>";


    HttpSend beforeSend(Response response);

}
