package cn.heshiqian.hotpothttp.core.plugin;

public enum ProxyTarget {
    REQUEST,
    RESPONSE,

    OTHERS;

    public static ProxyTarget convert(String target){
        return ProxyTarget.valueOf(target.toUpperCase());
    }
}
