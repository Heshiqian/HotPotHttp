package cn.heshiqian.hotpothttp.module.cookie;

public class CookieInvalidException extends RuntimeException{

    public CookieInvalidException(String cookieStr) {
        super("cookie :"+cookieStr+" is invalid, this cookie will not be used.");
    }
}
