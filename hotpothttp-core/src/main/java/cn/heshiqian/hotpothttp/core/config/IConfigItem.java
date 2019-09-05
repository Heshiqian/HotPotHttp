package cn.heshiqian.hotpothttp.core.config;

public interface IConfigItem {

    /**
     * return this ConfigItem name;
     * @return name
     */
    String itemTag();

    /**
     * add value
     * @param key key
     * @param newVal new value
     */
    void replace(String key, String newVal);

    String printObject();
}
