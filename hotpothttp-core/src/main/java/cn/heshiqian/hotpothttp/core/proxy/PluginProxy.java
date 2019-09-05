package cn.heshiqian.hotpothttp.core.proxy;

import cn.heshiqian.hotpothttp.core.pojo.Bundle;

import java.util.Map;

public interface PluginProxy {

    void proxy(Map<Bundle.Type, Bundle> bundleMap);

}
