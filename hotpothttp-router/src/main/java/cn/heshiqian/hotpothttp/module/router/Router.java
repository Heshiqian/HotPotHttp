package cn.heshiqian.hotpothttp.module.router;

import cn.heshiqian.hotpothttp.core.addtion.Log;
import cn.heshiqian.hotpothttp.core.config.Configuration;
import cn.heshiqian.hotpothttp.core.plugin.PluginManager;
import cn.heshiqian.hotpothttp.core.plugin.ProxyTarget;
import cn.heshiqian.hotpothttp.core.pojo.Bundle;
import cn.heshiqian.hotpothttp.core.proxy.PluginProxy;
import cn.heshiqian.hotpothttp.core.proxy.Proxy;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 注解方式初始化
 */
public class Router implements PluginProxy {

    private static final RouterPlugin plugin = new RouterPlugin();
    private File routerFile;
    private static Map<String, String> link;
    private PluginManager pluginManager;

    public static RouterPlugin getPluginInfo() {
        return plugin;
    }

    @Override
    public void proxy(Map<Bundle.Type, Bundle> bundleMap) {
        System.out.println("路由初始化");
        String confDir = Configuration.getArg(Configuration.CONF_DIR, String.class);
        //生成配置文件
        routerFile = new File(confDir + File.separator + "router.conf");
        if (!routerFile.exists()){
            try {
                Log.log(routerFile.createNewFile()?"创建路由配置文件失败！":"路由配置:"+routerFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                Log.err("可能权限不足,创建路由配置文件失败!");
            }
        }

        link = Tool.confLinkToMap(routerFile);

        pluginManager = Proxy.getPluginManager();

        pluginManager.registerPlugin(getPluginInfo(), ProxyTarget.REQUEST, new RouterRequest());
        pluginManager.registerPlugin(getPluginInfo(), ProxyTarget.RESPONSE, new RouterResponse());

    }

    public static Map<String, String> getLink() {
        return link;
    }
}
