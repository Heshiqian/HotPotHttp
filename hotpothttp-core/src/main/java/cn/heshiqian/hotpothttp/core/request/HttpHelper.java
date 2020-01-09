package cn.heshiqian.hotpothttp.core.request;

import cn.heshiqian.hotpothttp.core.addtion.Log;
import cn.heshiqian.hotpothttp.core.exception.HotPotHttpException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HttpHelper {


    public static Http analyze(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        int endEnterCount=0;
        String first=null;
        ArrayList<String> headList=new ArrayList<>();
        int emptyReadCount=0;
        while (emptyReadCount < 3){
            if (endEnterCount>=1) break;
            if (first==null){
                //第一行
                first = bufferedReader.readLine();
                //如果多次读出空行，则抛弃此次请求
                emptyReadCount++;
                continue;
            }
            String line = bufferedReader.readLine();
            if ("".equals(line)){
                endEnterCount++;
                continue;
            }
            headList.add(line);
        }
        String[] httpProtocol = first.split(" ");
        if (httpProtocol.length!=3)
            throw new HotPotHttpException("http protocol information invalid");
        String methodStr = httpProtocol[0];
        Http.Method method = Http.Method.valueOf(methodStr.toUpperCase());
        String pathStr = httpProtocol[1];
        String[] path = pathStr.split("\\?");
        String realPath="/";
        String parameter="";
        for (int i = 0; i < path.length; i++) {
            if (i==0)
                realPath=path[0];
            else
                parameter=path[i];
        }
        String httpVersion = httpProtocol[2];
        HttpImpl http = new HttpImpl(method,
                httpVersion,
                realPath,
                parameter,
                headList.toArray(new String[]{}),
                socket.getInetAddress());
        http.debug();
        return http;
    }

    public static Map<String,String> parseParameter(String parameter){
        HashMap<String, String> map = new HashMap<>();
        String[] split = parameter.split("&");
        for (int i = 0; i < split.length; i++) {
            String oneParam = split[i];
            String[] kv = oneParam.split("=");
            if (kv.length!=2)continue;//长度不合法,舍弃
            String key = kv[0];
            String param = kv[1];
            map.put(key,param);
        }
        return map;
    }

}
