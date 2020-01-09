package cn.heshiqian.hotpothttp.module.router;

import cn.heshiqian.hotpothttp.core.plugin.Plugin;

public class RouterPlugin implements Plugin {
    @Override
    public String name() {
        return "RouterPlugin-HotPotHttp";
    }

    @Override
    public long version() {
        return 1;
    }

    @Override
    public String desc() {
        return "简单的路径映射，可将请求指向其他资源";
    }
}
