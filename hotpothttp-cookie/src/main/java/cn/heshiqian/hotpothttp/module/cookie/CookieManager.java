package cn.heshiqian.hotpothttp.module.cookie;

import cn.heshiqian.hotpothttp.core.addtion.Log;
import cn.heshiqian.hotpothttp.core.request.RequestHead;

import java.util.HashMap;
import java.util.Map;

public class CookieManager implements CookieRequest {

    private Map<String, Cookie> cookieMap = new HashMap<>();

    public CookieManager(){

    }

    public CookieManager(String cookies){
        if (cookies==null) return;
        String[] split = cookies.split(";");
        for (int i = 0; i < split.length; i++) {
            String oneCookie = split[i];
            if (oneCookie.contains("=")){
                String[] ckv = oneCookie.trim().split("=");
                if (ckv.length!=2) throw new CookieInvalidException(oneCookie);
                String key = ckv[0];
                String value = ckv[1];
                cookieMap.put(key,new Cookie(key,value));
            }
        }
    }

    public CookieManager(RequestHead requestHead) {
        String cookies = requestHead.getHeader("cookie");
        if (cookies==null) return;
        String[] split = cookies.split(";");
        for (int i = 0; i < split.length; i++) {
            String oneCookie = split[i];
            if (oneCookie.contains("=")){
                String[] ckv = oneCookie.trim().split("=");
                if (ckv.length!=2) throw new CookieInvalidException(oneCookie);
                String key = ckv[0];
                String value = ckv[1];
                cookieMap.put(key,new Cookie(key,value));
            }
        }
    }

    @Override
    public void addCookie(Cookie cookie) {
        if (cookie == null) {
            throw new IllegalArgumentException("cookie is null");
        }
        cookieMap.put(cookie.getKey(),cookie);
    }

    @Override
    public void removeCookie(Cookie cookie) {
        if (cookie == null) {
            throw new IllegalArgumentException("cookie is null");
        }
        cookieMap.remove(cookie.getKey(),cookie);
    }

    @Override
    public boolean isHasCookie(String cookieName) {
        return cookieName.contains(cookieName);
    }

    @Override
    public Cookie getCookie(String cookieName){
        return cookieMap.get(cookieName);
    }

    /**
     * only in debug
     */
    public void printCookies(){
        Log.debug(cookieMap.toString());
    }
}
