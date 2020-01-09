package cn.heshiqian.hotpothttp.core.plugin;

import cn.heshiqian.hotpothttp.core.pojo.SimplePluginHolder;
import cn.heshiqian.hotpothttp.core.proxy.Proxy;

import java.util.LinkedList;
import java.util.Map;

public class PluginManager {

    private final Map<ProxyTarget, LinkedList<SimplePluginHolder>> proxyPluginList;

    public PluginManager() {
        proxyPluginList = Proxy.getInstance().getProxyPluginList();
    }


    public void registerPlugin(Plugin plugin,ProxyTarget target,Object instance){
        PMPHolder pmpHolder = new PMPHolder(plugin.name(), instance.getClass(), instance, plugin.version(), plugin.desc());
        LinkedList<SimplePluginHolder> holders = proxyPluginList.computeIfAbsent(target, target1 -> new LinkedList<>());
        holders.push(pmpHolder);
    }


    static class PMPHolder extends SimplePluginHolder{
        private long version;
        private String desc;

        public PMPHolder(String pluginName, Class pluginClass, Object instance, long version, String desc) {
            super(pluginName, pluginClass, instance);
            this.version = version;
            this.desc = desc;
        }

        public long getVersion() {
            return version;
        }

        public String getDesc() {
            return desc;
        }

    }
}
