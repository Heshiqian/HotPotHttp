package cn.heshiqian.hotpothttp.core.pojo;

public class SimplePluginHolder {

    private String pluginName;
    private Class pluginClass;
    private Object instance;

    @Override
    public String toString() {
        return "SimplePluginHolder{" +
                "pluginName='" + pluginName + '\'' +
                ", pluginClass=" + pluginClass +
                ", instance=" + instance +
                '}';
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public Class getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(Class pluginClass) {
        this.pluginClass = pluginClass;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public SimplePluginHolder() {
    }

    public SimplePluginHolder(String pluginName, Class pluginClass, Object instance) {
        this.pluginName = pluginName;
        this.pluginClass = pluginClass;
        this.instance = instance;
    }
}
