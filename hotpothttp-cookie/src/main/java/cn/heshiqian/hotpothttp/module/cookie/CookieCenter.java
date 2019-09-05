package cn.heshiqian.hotpothttp.module.cookie;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CookieCenter {

    private static class CookieCenterHolder{
        private static final CookieCenter holder=new CookieCenter();
    }
    private CookieCenter(){}
    public static CookieCenter getInstance() {
        return CookieCenterHolder.holder;
    }

    private static ConcurrentHashMap<String,CookieManager> cookieMap = new ConcurrentHashMap<>();

    public String getID(){
        return UUID.randomUUID().toString();
    }

    public void write(String id,CookieManager cookieManager){
        cookieMap.put(id,cookieManager);
    }

    public CookieManager read(String id){
        return cookieMap.get(id);
    }

    public void remove(String id){
        cookieMap.remove(id);
    }

}
