package cn.heshiqian.hotpothttp.module.router;

import cn.heshiqian.hotpothttp.core.plugin.abs.RequestPlugin;
import cn.heshiqian.hotpothttp.core.pojo.Package;
import cn.heshiqian.hotpothttp.core.request.Request;

import java.util.HashMap;
import java.util.Map;

public class RouterRequest extends RequestPlugin {

    public static final String ROUTER_ORIGIN = "Router-Origin";
    public static final String ROUTER_LINK_PATH = "Router-Link-Path";
    public static final String ROUTER_VERSION = "Router-Version";

    static {
        link = Router.getLink();
    }
    private static Map<String, String> link;

    private static final Package.PackageType packageType =
            new Package.PackageType("Router-Private-Data","路由数据传输");

    public static Package.PackageType getPackageType() {
        return packageType;
    }

    @Override
    public boolean process(Request request) {
        String path = request.getPath();
        if (link==null||link.size()==0) return false;
        String linkPath = link.get(path);
        if (linkPath==null||linkPath.isEmpty()) return false;
        request.setPath(linkPath);
        //创建附带数据
        Package<Map<String, String>> pack = new Package<>();
        HashMap<String, String> ownObj = new HashMap<>();
        pack.setOwnObj(ownObj);

        ownObj.put(ROUTER_ORIGIN,path);
        ownObj.put(ROUTER_LINK_PATH,linkPath);
        ownObj.put(ROUTER_VERSION,Router.getPluginInfo().version()+"");

        request.addPackage(packageType, pack);
        //继续向下传递
        return false;
    }

}
