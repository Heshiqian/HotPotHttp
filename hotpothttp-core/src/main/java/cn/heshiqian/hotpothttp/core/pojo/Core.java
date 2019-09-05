package cn.heshiqian.hotpothttp.core.pojo;

import cn.heshiqian.hotpothttp.core.config.IConfigItem;

public final class Core implements IConfigItem {

    private String port="889";
    private String ip="127.0.0.1";
    private String className="";

    private String nio="false";


    @Override
    public String itemTag() {
        return null;
    }

    @Override
    public void replace(String key, String newVal) {

    }

    @Override
    public String printObject() {
        return null;
    }
}