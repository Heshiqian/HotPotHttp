package cn.heshiqian.hotpothttp.module.router;

import cn.heshiqian.hotpothttp.core.addtion.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class Tool {

    public static Map<String,String> confLinkToMap(File confFile){
        Map<String,String> map=new HashMap<>();
        try{
            FileInputStream fis = new FileInputStream(confFile);
            InputStreamReader in = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(in);
            String line;
            while ((line=reader.readLine())!=null){
                if (line.startsWith("#"))continue;
                if (line.isEmpty())continue;
                int indexOfEquals = line.lastIndexOf("=");
                if (indexOfEquals==-1){
                    Log.err("路由："+line+" 格式错误! 缺少'='");
                    continue;
                }
                String originPath = line.substring(0, indexOfEquals).replace("/","\\");
                String linkPath = line.substring(indexOfEquals + 1).replace("/","\\");
                Log.log("Link OP:"+originPath+" to LP:"+linkPath);
                map.put(originPath,linkPath);
            }
        }catch (IOException e){
            e.printStackTrace();
            Log.err("路由配置读取失败,将不会使用路由配置!");
        }
        return map;
    }


}
