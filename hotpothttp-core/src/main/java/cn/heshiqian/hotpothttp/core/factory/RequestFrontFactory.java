package cn.heshiqian.hotpothttp.core.factory;

import cn.heshiqian.hotpothttp.core.request.*;

import java.net.InetAddress;
import java.util.Map;

public class RequestFrontFactory implements RequestProcessor {

    @Override
    public Request receive(Http http) {

        Http.Method method = http.method();
        InetAddress address = http.address();
        String ip = address.getHostAddress();

        RequestImpl request = new RequestImpl(method.toString(),address,ip);

        String[] head = http.head();
        RequestHeadImpl requestHead = new RequestHeadImpl();
        for (String hstr:head){
            String[] onelineHead = hstr.split(":");
            if (onelineHead.length!=2) continue;
            String headType = onelineHead[0];
            String headContent = onelineHead[1];//除去冒号空格
            if (headContent.startsWith(" ")){
                //证明有个空格
                headContent = headContent.substring(1);
            }
            requestHead.put(headType,headContent);
        }
        request.setHeader(requestHead);
        request.setStatus(Status.OK);//默认请求就是200
        String path = http.path().replace("/","\\");//斜线换方向
        request.setPath(path);
        request.setType("Client Request");
        Map<String, String> map = HttpHelper.parseParameter(http.parameter());
        request.setParameter(map);
        return request;
    }
}
