package cn.heshiqian.hotpothttp.module.cookie;

public interface CookieRequest {

    void addCookie(Cookie cookie);

    void removeCookie(Cookie cookie);

    boolean isHasCookie(String cookieName);

    Cookie getCookie(String cookieName);
}
