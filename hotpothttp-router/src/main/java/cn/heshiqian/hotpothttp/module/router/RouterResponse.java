package cn.heshiqian.hotpothttp.module.router;

import cn.heshiqian.hotpothttp.core.plugin.abs.ResponsePlugin;
import cn.heshiqian.hotpothttp.core.pojo.Package;
import cn.heshiqian.hotpothttp.core.response.Response;

import java.util.Map;

public class RouterResponse extends ResponsePlugin {

    @Override
    public boolean process(Response response) {
        Package<Map> aPackage = response.getPackage(RouterRequest.getPackageType());
        if (aPackage == null)return false;
        Map ownObj = aPackage.getOwnObj();
        if (ownObj==null)return false;
        String origin = String.valueOf(ownObj.get(RouterRequest.ROUTER_LINK_PATH));
        String version = String.valueOf(ownObj.get(RouterRequest.ROUTER_VERSION));
        response.addHeader("Router-Origin-Url",origin);
        response.addHeader("Router-Version",version);
        return false;
    }
}
