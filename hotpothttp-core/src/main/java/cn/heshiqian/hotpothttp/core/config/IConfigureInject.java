package cn.heshiqian.hotpothttp.core.config;

public interface IConfigureInject {

    void injectConfig(String rootTag, String val) throws NoSuchFieldException, IllegalAccessException;

}
