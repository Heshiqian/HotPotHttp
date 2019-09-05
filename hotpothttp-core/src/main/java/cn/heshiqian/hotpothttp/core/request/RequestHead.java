package cn.heshiqian.hotpothttp.core.request;

import java.util.Map;

public interface RequestHead extends Map<String,String> {

    void printHead();

    String getHeader(String key);

    Map<String,String> getHeaders();

}
