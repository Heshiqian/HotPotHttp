package cn.heshiqian.hotpothttp.module.cookie;

import java.io.Serializable;

public class Cookie implements Serializable {

    private String key;
    private String value;

    @Override
    public String toString() {
        return "Cookie{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
